package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

@WebServlet("/guess")
public class GuessNumberServlet extends HttpServlet {
    private static final int MAX_NUMBER = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("number") == null) {
            session.setAttribute("number", new Random().nextInt(MAX_NUMBER) + 1);
            session.setAttribute("attempts", 0);
        }
        request.getRequestDispatcher("/index.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer number = (Integer) session.getAttribute("number");
        Integer attempts = (Integer) session.getAttribute("attempts");
        if (number == null || attempts == null) {
            number = new Random().nextInt(MAX_NUMBER) + 1;
            attempts = 0;
            session.setAttribute("number", number);
            session.setAttribute("attempts", attempts);
        }
        String guessStr = request.getParameter("guess");
        String message;
        boolean correct = false;
        if (guessStr != null) {
            try {
                int guess = Integer.parseInt(guessStr);
                attempts++;
                session.setAttribute("attempts", attempts);
                if (guess < number) {
                    message = "Too low! Try again.";
                } else if (guess > number) {
                    message = "Too high! Try again.";
                } else {
                    message = "Congratulations! You guessed the number in " + attempts + " attempts.";
                    correct = true;
                }
            } catch (NumberFormatException e) {
                message = "Please enter a valid number.";
            }
        } else {
            message = "Please enter your guess.";
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Guess the Number</title>");
        out.println("    <link rel='stylesheet' href='style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <h1>Guess the Number</h1>");
        out.println("        <form method='post' action='guess'>");
        if (!correct) {
            out.println("            <input type='number' name='guess' min='1' max='100' required placeholder='Enter a number (1-100)'>");
            out.println("            <button type='submit'>Guess</button>");
        } else {
            out.println("            <a href='reset' class='reset-btn'>Play Again</a>");
        }
        out.println("        </form>");
        if (correct) {
            out.println("        <p class='message win-message'>" + message + "</p>");
        } else {
            out.println("        <p class='message'>" + message + "</p>");
        }
        out.println("        <p>Attempts: " + attempts + "</p>");
        out.println("    </div>");
        
        // Add confetti container and script
        out.println("    <div class='confetti' id='confetti'></div>");
        out.println("    <script>");
        out.println("        function createRealisticConfetti() {");
        out.println("            const confettiContainer = document.getElementById('confetti');");
        out.println("            confettiContainer.innerHTML = '';");
        out.println("            confettiContainer.style.display = 'block';");
        out.println("");
        out.println("            const colors = [");
        out.println("                '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57',");
        out.println("                '#ff9ff3', '#54a0ff', '#5f27cd', '#00d2d3', '#ff9f43',");
        out.println("                '#10ac84', '#ee5a24', '#0abde3', '#f368e0', '#fad390',");
        out.println("                '#6c5ce7', '#a29bfe', '#fd79a8', '#fdcb6e', '#e17055',");
        out.println("                '#74b9ff', '#0984e3', '#00b894', '#00cec9', '#6c5ce7',");
        out.println("                '#a29bfe', '#fd79a8', '#fdcb6e', '#e84393', '#2d3436'");
        out.println("            ];");
        out.println("");
        out.println("            const shapes = ['square', 'rect', 'string', 'circle', 'triangle'];");
        out.println("            const animations = ['confetti-fall', 'confetti-left', 'confetti-right', 'confetti-center'];");
        out.println("");
        out.println("            for (let i = 0; i < 150; i++) {");
        out.println("                const confetti = document.createElement('div');");
        out.println("                const shape = shapes[Math.floor(Math.random() * shapes.length)];");
        out.println("                const color = colors[Math.floor(Math.random() * colors.length)];");
        out.println("                const animation = animations[Math.floor(Math.random() * animations.length)];");
        out.println("");
        out.println("                confetti.className = `confetti-piece confetti-${shape}`;");
        out.println("");
        out.println("                if (shape === 'triangle') {");
        out.println("                    confetti.style.borderBottomColor = color;");
        out.println("                } else {");
        out.println("                    confetti.style.backgroundColor = color;");
        out.println("                }");
        out.println("");
        out.println("                const startPos = Math.random() * 120 - 10;");
        out.println("                confetti.style.left = startPos + '%';");
        out.println("                confetti.style.top = Math.random() * -100 - 50 + 'px';");
        out.println("");
        out.println("                const duration = (Math.random() * 2.5 + 1.5) + 's';");
        out.println("                const delay = Math.random() * 2 + 's';");
        out.println("                const drift = (Math.random() * 400 - 200) + 'px';");
        out.println("                const rotation = (Math.random() * 720 + 360) + 'deg';");
        out.println("");
        out.println("                confetti.style.setProperty('--drift', drift);");
        out.println("                confetti.style.setProperty('--rotation', rotation);");
        out.println("");
        out.println("                confetti.style.animation = `${animation} ${duration} linear ${delay} infinite`;");
        out.println("                confetti.style.opacity = Math.random() * 0.3 + 0.7;");
        out.println("");
        out.println("                confettiContainer.appendChild(confetti);");
        out.println("            }");
        out.println("");
        out.println("            setTimeout(() => {");
        out.println("                confettiContainer.style.display = 'none';");
        out.println("            }, 6000);");
        out.println("        }");
        
        if (correct) {
            out.println("        // Auto-trigger confetti animation when user wins");
            out.println("        setTimeout(function() {");
            out.println("            createRealisticConfetti();");
            out.println("        }, 500);");
        }
        out.println("    </script>");
        out.println("</body>");
        out.println("</html>");
    }
}
