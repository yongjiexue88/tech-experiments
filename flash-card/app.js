/* ============================================
   CompTIA Security+ Flashcard App — Logic
   ============================================ */

(function () {
    "use strict";

    // --- State ---
    let cards = [...ACRONYMS];
    let currentIndex = 0;
    let isFlipped = false;
    let mode = "acronym"; // "acronym" = show acronym first, "definition" = show definition first
    let starred = new Set(JSON.parse(localStorage.getItem("sec_starred") || "[]"));
    let filterStarred = false;

    // --- DOM refs ---
    const card = document.getElementById("card");
    const frontLabel = document.getElementById("frontLabel");
    const frontText = document.getElementById("frontText");
    const backLabel = document.getElementById("backLabel");
    const backText = document.getElementById("backText");
    const counter = document.getElementById("cardCounter");
    const progressBar = document.getElementById("progressBar");
    const progressText = document.getElementById("progressText");
    const btnPrev = document.getElementById("btnPrev");
    const btnNext = document.getElementById("btnNext");
    const btnStar = document.getElementById("btnStar");
    const btnShuffle = document.getElementById("btnShuffle");
    const btnAll = document.getElementById("btnAll");
    const btnStarred = document.getElementById("btnStarred");
    const btnModeAcronym = document.getElementById("btnModeAcronym");
    const btnModeDefinition = document.getElementById("btnModeDefinition");
    const searchInput = document.getElementById("searchInput");
    const cardContainer = document.getElementById("cardContainer");

    // --- Helpers ---
    function saveStarred() {
        localStorage.setItem("sec_starred", JSON.stringify([...starred]));
    }

    function shuffle(arr) {
        for (let i = arr.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [arr[i], arr[j]] = [arr[j], arr[i]];
        }
        return arr;
    }

    function rebuildDeck() {
        const query = searchInput.value.trim().toLowerCase();
        let pool = filterStarred
            ? ACRONYMS.filter(c => starred.has(c.acronym))
            : [...ACRONYMS];

        if (query) {
            pool = pool.filter(c =>
                c.acronym.toLowerCase().includes(query) ||
                c.definition.toLowerCase().includes(query)
            );
        }

        cards = pool;
        currentIndex = 0;
        unflip();
        render();
    }

    // --- Render ---
    function render() {
        if (cards.length === 0) {
            frontLabel.textContent = "";
            frontText.textContent = "No cards found";
            backLabel.textContent = "";
            backText.textContent = "";
            counter.textContent = "0 / 0";
            progressBar.style.width = "0%";
            progressText.textContent = "0 / 0";
            btnStar.textContent = "☆";
            btnStar.classList.remove("starred");
            return;
        }

        const c = cards[currentIndex];

        if (mode === "acronym") {
            frontLabel.textContent = "ACRONYM";
            frontText.textContent = c.acronym;
            backLabel.textContent = "DEFINITION";
            backText.textContent = c.definition;
        } else {
            frontLabel.textContent = "DEFINITION";
            frontText.textContent = c.definition;
            backLabel.textContent = "ACRONYM";
            backText.textContent = c.acronym;
        }

        // Adjust front text size based on content length
        const frontLen = frontText.textContent.length;
        if (frontLen > 60) {
            frontText.style.fontSize = "1rem";
        } else if (frontLen > 30) {
            frontText.style.fontSize = "1.4rem";
        } else {
            frontText.style.fontSize = "";
        }

        counter.textContent = `${currentIndex + 1} / ${cards.length}`;

        const pct = ((currentIndex + 1) / cards.length) * 100;
        progressBar.style.width = pct + "%";
        progressText.textContent = `${currentIndex + 1} / ${cards.length}`;

        // Star
        if (starred.has(c.acronym)) {
            btnStar.textContent = "★";
            btnStar.classList.add("starred");
        } else {
            btnStar.textContent = "☆";
            btnStar.classList.remove("starred");
        }
    }

    function unflip() {
        isFlipped = false;
        card.classList.remove("flipped");
    }

    function flip() {
        if (cards.length === 0) return;
        isFlipped = !isFlipped;
        card.classList.toggle("flipped", isFlipped);
    }

    function slideAnim(dir) {
        cardContainer.classList.remove("slide-left", "slide-right");
        void cardContainer.offsetWidth; // reflow
        cardContainer.classList.add(dir);
        setTimeout(() => cardContainer.classList.remove(dir), 350);
    }

    function go(delta) {
        if (cards.length === 0) return;
        currentIndex = (currentIndex + delta + cards.length) % cards.length;
        unflip();
        slideAnim(delta > 0 ? "slide-left" : "slide-right");
        render();
    }

    // --- Event listeners ---
    card.addEventListener("click", flip);

    btnPrev.addEventListener("click", () => go(-1));
    btnNext.addEventListener("click", () => go(1));

    btnStar.addEventListener("click", () => {
        if (cards.length === 0) return;
        const a = cards[currentIndex].acronym;
        if (starred.has(a)) starred.delete(a); else starred.add(a);
        saveStarred();
        render();
    });

    btnShuffle.addEventListener("click", () => {
        shuffle(cards);
        currentIndex = 0;
        unflip();
        render();
    });

    btnAll.addEventListener("click", () => {
        filterStarred = false;
        btnAll.classList.add("active");
        btnStarred.classList.remove("active");
        rebuildDeck();
    });

    btnStarred.addEventListener("click", () => {
        filterStarred = true;
        btnStarred.classList.add("active");
        btnAll.classList.remove("active");
        rebuildDeck();
    });

    btnModeAcronym.addEventListener("click", () => {
        mode = "acronym";
        btnModeAcronym.classList.add("active");
        btnModeDefinition.classList.remove("active");
        unflip();
        render();
    });

    btnModeDefinition.addEventListener("click", () => {
        mode = "definition";
        btnModeDefinition.classList.add("active");
        btnModeAcronym.classList.remove("active");
        unflip();
        render();
    });

    searchInput.addEventListener("input", rebuildDeck);

    // --- Keyboard shortcuts ---
    document.addEventListener("keydown", (e) => {
        // Don't intercept when typing in search
        if (document.activeElement === searchInput && e.key !== "Escape") return;

        switch (e.key) {
            case "ArrowRight":
            case "l":
                e.preventDefault();
                go(1);
                break;
            case "ArrowLeft":
            case "h":
                e.preventDefault();
                go(-1);
                break;
            case " ":
                e.preventDefault();
                flip();
                break;
            case "s":
            case "S":
                e.preventDefault();
                btnStar.click();
                break;
            case "/":
                e.preventDefault();
                searchInput.focus();
                break;
            case "Escape":
                searchInput.blur();
                searchInput.value = "";
                rebuildDeck();
                break;
        }
    });

    // --- Touch swipe support ---
    let touchStartX = 0;
    cardContainer.addEventListener("touchstart", (e) => {
        touchStartX = e.changedTouches[0].clientX;
    }, { passive: true });

    cardContainer.addEventListener("touchend", (e) => {
        const dx = e.changedTouches[0].clientX - touchStartX;
        if (Math.abs(dx) > 50) {
            go(dx < 0 ? 1 : -1);
        }
    }, { passive: true });

    // --- Init ---
    render();
})();
