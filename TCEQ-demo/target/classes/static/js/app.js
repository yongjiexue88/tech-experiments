/**
 * TCEQ Environmental Compliance Dashboard — JavaScript Application
 *
 * Vanilla JavaScript SPA that fetches data from the REST API and renders
 * the compliance dashboard: summary stats, Chart.js visualizations, and
 * a searchable/filterable facilities table.
 */

/* ═══════════════════════════════════════════════════════════════
   API Communication Layer
   ═══════════════════════════════════════════════════════════════ */

const API = {
    /** Fetches JSON from a relative URL with error handling. */
    async get(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return await response.json();
        } catch (error) {
            console.error(`API error for ${url}:`, error);
            return null;
        }
    },

    getDashboardStats() { return this.get('/api/dashboard/stats'); },
    getViolationsBySeverity() { return this.get('/api/dashboard/violations-by-severity'); },
    getPermitsByStatus() { return this.get('/api/dashboard/permits-by-status'); },
    getInspectionsByResult() { return this.get('/api/dashboard/inspections-by-result'); },
    getFacilities() { return this.get('/api/facilities'); },
    getCounties() { return this.get('/api/facilities/counties'); },
};


/* ═══════════════════════════════════════════════════════════════
   Chart Configuration
   ═══════════════════════════════════════════════════════════════ */

const CHART_COLORS = {
    CRITICAL: '#EF4444',
    MAJOR: '#F59E0B',
    MINOR: '#3B82F6',
    ACTIVE: '#22C55E',
    EXPIRED: '#EF4444',
    REVOKED: '#A855F7',
    PENDING: '#F59E0B',
    PASS: '#22C55E',
    FAIL: '#EF4444',
    FOLLOW_UP: '#F59E0B',
};

const chartDefaults = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            position: 'bottom',
            labels: {
                color: '#8896A8',
                font: { family: 'Inter', size: 12 },
                padding: 16,
                usePointStyle: true,
                pointStyleWidth: 12,
            },
        },
    },
};

/** Chart instances (for cleanup on refresh) */
let charts = {};

function destroyCharts() {
    Object.values(charts).forEach(c => c.destroy());
    charts = {};
}


/* ═══════════════════════════════════════════════════════════════
   Rendering Functions
   ═══════════════════════════════════════════════════════════════ */

/**
 * Populates the four header statistic cards.
 */
function renderStats(stats) {
    if (!stats) return;

    document.getElementById('val-facilities').textContent = stats.totalFacilities ?? '—';
    document.getElementById('val-permits').textContent = stats.totalPermits ?? '—';
    document.getElementById('val-violations').textContent = stats.openViolations ?? '—';
    document.getElementById('val-penalties').textContent = formatCurrency(stats.totalPenalties);
}

/**
 * Creates a doughnut chart for violations grouped by severity.
 */
function renderViolationsChart(data) {
    if (!data) return;
    const ctx = document.getElementById('chart-violations').getContext('2d');
    const labels = Object.keys(data);
    const values = Object.values(data);

    charts.violations = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: labels.map(l => CHART_COLORS[l] || '#64748B'),
                borderWidth: 0,
                hoverOffset: 6,
            }],
        },
        options: {
            ...chartDefaults,
            cutout: '65%',
        },
    });
}

/**
 * Creates a bar chart for permits grouped by status.
 */
function renderPermitsChart(data) {
    if (!data) return;
    const ctx = document.getElementById('chart-permits').getContext('2d');
    const labels = Object.keys(data);
    const values = Object.values(data);

    charts.permits = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: labels.map(l => CHART_COLORS[l] || '#64748B'),
                borderRadius: 6,
                barPercentage: 0.6,
            }],
        },
        options: {
            ...chartDefaults,
            plugins: {
                ...chartDefaults.plugins,
                legend: { display: false },
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { color: '#8896A8', font: { family: 'Inter', size: 12 } },
                },
                y: {
                    beginAtZero: true,
                    grid: { color: 'rgba(255,255,255,0.04)' },
                    ticks: {
                        color: '#8896A8',
                        font: { family: 'Inter', size: 12 },
                        stepSize: 1,
                    },
                },
            },
        },
    });
}

/**
 * Creates a doughnut chart for inspections grouped by result.
 */
function renderInspectionsChart(data) {
    if (!data) return;
    const ctx = document.getElementById('chart-inspections').getContext('2d');
    const labels = Object.keys(data);
    const values = Object.values(data);

    charts.inspections = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: labels.map(l => CHART_COLORS[l] || '#64748B'),
                borderWidth: 0,
                hoverOffset: 6,
            }],
        },
        options: {
            ...chartDefaults,
            cutout: '65%',
        },
    });
}

/**
 * Builds and populates the facilities table.
 */
function renderFacilitiesTable(facilities) {
    const tbody = document.getElementById('facilities-tbody');
    if (!facilities || facilities.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="table-loading">No facilities found.</td></tr>';
        return;
    }

    tbody.innerHTML = facilities.map(f => `
        <tr>
            <td><strong>${escapeHtml(f.facilityName)}</strong></td>
            <td><code>${escapeHtml(f.epaId)}</code></td>
            <td>${escapeHtml(f.facilityType)}</td>
            <td>${escapeHtml(f.city)}</td>
            <td>${escapeHtml(f.county)}</td>
            <td>${statusBadge(f.status)}</td>
            <td>${f.permits ?? '—'}</td>
            <td class="violation-count ${(f.openViolations && f.openViolations > 0) ? 'violation-count--has' : ''}">
                ${f.openViolations != null ? `${f.openViolations} open` : (f.violations ?? '—')}
            </td>
            <td>${scoreBadge(f.complianceScore)}</td>
        </tr>
    `).join('');
}

/**
 * Populates the county filter dropdown.
 */
function renderCountyFilter(counties) {
    const select = document.getElementById('filter-county');
    if (!counties) return;

    // Clear existing options (keep "All Counties")
    select.innerHTML = '<option value="">All Counties</option>';
    counties.forEach(c => {
        const opt = document.createElement('option');
        opt.value = c;
        opt.textContent = c;
        select.appendChild(opt);
    });
}


/* ═══════════════════════════════════════════════════════════════
   Helper Functions
   ═══════════════════════════════════════════════════════════════ */

function formatCurrency(amount) {
    if (amount == null) return '—';
    return '$' + Number(amount).toLocaleString('en-US', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
    });
}

function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function statusBadge(status) {
    if (!status) return '';
    const cls = status.toLowerCase();
    return `<span class="badge badge--${cls}">${status}</span>`;
}

function scoreBadge(score) {
    if (score == null) return '—';
    let cls = 'high';
    if (score < 50) cls = 'low';
    else if (score < 80) cls = 'medium';
    return `<span class="score-badge score-badge--${cls}">${score}</span>`;
}


/* ═══════════════════════════════════════════════════════════════
   Dashboard Initialization
   ═══════════════════════════════════════════════════════════════ */

/** All facilities cached for client-side filtering */
let allFacilities = [];

async function loadDashboard() {
    destroyCharts();

    // Fetch all data in parallel
    const [stats, violations, permits, inspections, facilities, counties] =
        await Promise.all([
            API.getDashboardStats(),
            API.getViolationsBySeverity(),
            API.getPermitsByStatus(),
            API.getInspectionsByResult(),
            API.getFacilities(),
            API.getCounties(),
        ]);

    // Render components
    renderStats(stats);
    renderViolationsChart(violations);
    renderPermitsChart(permits);
    renderInspectionsChart(inspections);
    renderFacilitiesTable(facilities);
    renderCountyFilter(counties);

    // Cache facilities for filtering
    allFacilities = facilities || [];
}

/* ── Search & Filter Event Listeners ──────────────────────────── */

document.addEventListener('DOMContentLoaded', () => {
    loadDashboard();

    const searchInput = document.getElementById('search-input');
    const countyFilter = document.getElementById('filter-county');

    let debounceTimer;

    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => filterFacilities(), 250);
    });

    countyFilter.addEventListener('change', () => filterFacilities());
});

function filterFacilities() {
    const searchTerm = document.getElementById('search-input').value.toLowerCase().trim();
    const county = document.getElementById('filter-county').value;

    let filtered = allFacilities;

    if (searchTerm) {
        filtered = filtered.filter(f =>
            (f.facilityName && f.facilityName.toLowerCase().includes(searchTerm)) ||
            (f.epaId && f.epaId.toLowerCase().includes(searchTerm)) ||
            (f.city && f.city.toLowerCase().includes(searchTerm))
        );
    }

    if (county) {
        filtered = filtered.filter(f => f.county === county);
    }

    renderFacilitiesTable(filtered);
}
