package com.example.demo.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class OpenAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    public String generateReadme(String code) {
        try {
            // Groq OpenAI-compatible endpoint
            URL url = new URL("https://api.groq.com/openai/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt =
                    "You are a senior software engineer.\n\n" +
                            "Generate a professional README.md for the following code.\n\n" +
                            "First, infer the main programming language (Java, Python, JavaScript, etc.) " +
                            "from the code itself and use that language consistently in the README.\n\n" +
                            "Formatting requirements:\n" +
                            "- Output ONLY valid Markdown for a README.md file.\n" +
                            "- Do not add explanations before or after the Markdown.\n" +
                            "- Start with a single H1 title line, e.g. '# Project Name'.\n" +
                            "- Use clear Markdown headings (##, ###) for sections.\n" +
                            "- Use fenced code blocks with language annotations (```bash, ```java, ```python, etc.).\n\n" +
                            "The README must include the following sections (use these exact headings):\n" +
                            "1. ## Overview\n" +
                            "   - High-level project description.\n" +
                            "2. ## Features\n" +
                            "   - Bullet list of key capabilities.\n" +
                            "3. ## Technology Stack\n" +
                            "   - Languages, frameworks, tools.\n" +
                            "4. ## Database Schema (if applicable)\n" +
                            "   - Describe entities/tables, key fields, and relationships.\n" +
                            "   - If there is no database, explicitly say that this project does not use a database.\n" +
                            "5. ## Setup & Installation\n" +
                            "   - Step-by-step instructions to get the project running (clone, install deps, build).\n" +
                            "6. ## How to Run\n" +
                            "   - Concrete commands or steps to start the application.\n" +
                            "7. ## Example Input & Output\n" +
                            "   - Provide at least one example showing sample input and the corresponding output.\n" +
                            "8. ## Future Improvements\n" +
                            "   - 3–5 realistic ideas for how this project could be enhanced.\n" +
                            "9. ## Optional: Visual Workflow Diagram\n" +
                            "   - If appropriate, include a simple Mermaid diagram in a ```mermaid code block showing the main flow " +
                            "     (e.g. user request -> controller -> service -> database / model).\n\n" +
                            "Code:\n" + code;

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject body = new JSONObject();
            body.put("model", "llama-3.3-70b-versatile"); // Groq model name
            body.put("messages", messages);
            body.put("temperature", 0.7);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8);
            String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();

            if (status != 200) {
                return "Error from Groq (" + status + "): " + response;
            }

            JSONObject jsonResponse = new JSONObject(response);

            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating README: " + e.getMessage();
        }
    }
}