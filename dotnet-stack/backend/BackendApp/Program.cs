using Microsoft.EntityFrameworkCore;
using BackendApp.Data;
using BackendApp.Models;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Configure SQLite
builder.Services.AddDbContext<AppDbContext>(opt => opt.UseSqlite("Data Source=todo.db"));

// Configure CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        builder =>
        {
            builder.AllowAnyOrigin()
                   .AllowAnyMethod()
                   .AllowAnyHeader();
        });
});

var app = builder.Build();

// Ensure the database is created
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.EnsureCreated();
}

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("AllowAll");
app.UseHttpsRedirection();

// Todo Endpoints (Minimal APIs)
var todoItems = app.MapGroup("/api/todoitems");

todoItems.MapGet("/", async (AppDbContext db) =>
    await db.TodoItems.ToListAsync());

todoItems.MapGet("/{id}", async (int id, AppDbContext db) =>
    await db.TodoItems.FindAsync(id)
        is TodoItem todo
            ? Results.Ok(todo)
            : Results.NotFound());

todoItems.MapPost("/", async (TodoItem todo, AppDbContext db) =>
{
    db.TodoItems.Add(todo);
    await db.SaveChangesAsync();
    return Results.Created($"/api/todoitems/{todo.Id}", todo);
});

todoItems.MapPut("/{id}", async (int id, TodoItem inputTodo, AppDbContext db) =>
{
    var todo = await db.TodoItems.FindAsync(id);
    if (todo is null) return Results.NotFound();

    todo.Name = inputTodo.Name;
    todo.IsComplete = inputTodo.IsComplete;

    await db.SaveChangesAsync();
    return Results.NoContent();
});

todoItems.MapDelete("/{id}", async (int id, AppDbContext db) =>
{
    if (await db.TodoItems.FindAsync(id) is TodoItem todo)
    {
        db.TodoItems.Remove(todo);
        await db.SaveChangesAsync();
        return Results.Ok(todo);
    }
    return Results.NotFound();
});

app.Run();
