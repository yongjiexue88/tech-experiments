using Microsoft.EntityFrameworkCore;
using BackendApp.Models;

namespace BackendApp.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }
    public DbSet<TodoItem> TodoItems => Set<TodoItem>();
}
