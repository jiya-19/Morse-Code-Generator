# 🔤 Morse Code Translator (Java + HTML)

A simple **Morse Code Translator/Generator** built with a lightweight Java backend and a clean HTML/JS frontend.
The project allows you to:

* Convert **plain text → Morse code**
* Convert **Morse code → plain text**
* Interact through a minimal web interface (`index.html`)
* Or use REST endpoints (`/encode`, `/decode`) programmatically

---

## 🚀 Features

* **Backend:** Java `HttpServer` (no external dependencies)

  * `POST /encode` → Returns Morse code for given text
  * `POST /decode` → Returns plain text for given Morse code
* **Frontend:** Responsive HTML + CSS + Vanilla JS interface
* **CORS enabled** → Works with separate frontend file
* Clean UI with support for letters, numbers, and `/` as word separator

---

## 🛠️ Setup & Run

1. Clone the repo:

   ```bash
   git clone https://github.com/your-username/morse-code-translator.git
   cd morse-code-translator
   ```

2. Compile and run the backend:

   ```bash
   javac MorseCodeServer.java
   java MorseCodeServer
   ```

   Server runs on **[http://localhost:8081/](http://localhost:8081/)**

3. Open `index.html` in your browser.

   * Type plain text and click **Encode →**
   * Paste Morse and click **← Decode**

---

## 📡 API Usage

Example using `curl`:

```bash
# Encode text
curl -X POST http://localhost:8081/encode -d "HELLO WORLD"

# Decode Morse
curl -X POST http://localhost:8081/decode -d ".... . .-.. .-.. --- / .-- --- .-. .-.. -.."
```

---

## 📂 Project Structure

```
├── MorseCodeServer.java   # Java backend (API server)
└── index.html             # Frontend UI
```

---

## 💡 Future Enhancements

* Add support for punctuation
* Play Morse code as audio beeps
* Deployable as a lightweight web app

---

## 📜 License

This project is open-source under the **MIT License**.
