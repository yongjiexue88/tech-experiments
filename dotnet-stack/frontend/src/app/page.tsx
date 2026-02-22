"use client";

import { useState, useEffect } from "react";
import styles from "../styles/Home.module.css";

interface TodoItem {
  id: number;
  name: string;
  isComplete: boolean;
}

const API_URL = "http://localhost:5000/api/todoitems";

export default function Home() {
  const [todos, setTodos] = useState<TodoItem[]>([]);
  const [newTodo, setNewTodo] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      const response = await fetch(API_URL);
      const data = await response.json();
      setTodos(data);
    } catch (error) {
      console.error("Error fetching todos:", error);
    } finally {
      setLoading(false);
    }
  };

  const addTodo = async () => {
    if (!newTodo.trim()) return;
    try {
      const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: newTodo, isComplete: false }),
      });
      if (response.ok) {
        setNewTodo("");
        fetchTodos();
      }
    } catch (error) {
      console.error("Error adding todo:", error);
    }
  };

  const toggleTodo = async (todo: TodoItem) => {
    try {
      const response = await fetch(`${API_URL}/${todo.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...todo, isComplete: !todo.isComplete }),
      });
      if (response.ok) {
        fetchTodos();
      }
    } catch (error) {
      console.error("Error toggling todo:", error);
    }
  };

  const deleteTodo = async (id: number) => {
    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
      });
      if (response.ok) {
        fetchTodos();
      }
    } catch (error) {
      console.error("Error deleting todo:", error);
    }
  };

  return (
    <div className={styles.main}>
      <div className={styles.card}>
        <h1 className={styles.title}>DotNet Stack</h1>

        <div className={styles.inputGroup}>
          <input
            type="text"
            className={styles.input}
            placeholder="Add a new task..."
            value={newTodo}
            onChange={(e) => setNewTodo(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && addTodo()}
          />
          <button className={styles.button} onClick={addTodo}>Add</button>
        </div>

        {loading ? (
          <p style={{ textAlign: "center" }}>Loading tasks...</p>
        ) : (
          <div className={styles.todoList}>
            {todos.length === 0 ? (
              <p style={{ textAlign: "center", opacity: 0.5 }}>No tasks yet.</p>
            ) : (
              todos.map((todo) => (
                <div key={todo.id} className={styles.todoItem}>
                  <input
                    type="checkbox"
                    checked={todo.isComplete}
                    onChange={() => toggleTodo(todo)}
                  />
                  <span className={`${styles.todoName} ${todo.isComplete ? styles.completed : ""}`}>
                    {todo.name}
                  </span>
                  <button
                    className={styles.button}
                    style={{ background: "#ff4d4d", padding: "0.4rem 0.8rem" }}
                    onClick={() => deleteTodo(todo.id)}
                  >
                    Delete
                  </button>
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
}
