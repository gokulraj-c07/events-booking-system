Integrated-Event-Management-System-For-Provider-And-User
========================================================

EventHub - Events Booking System
==================================

Project Description
-------------------
EventHub is a comprehensive web-based platform designed to simplify the process of booking event services. It connects users with service providers, allowing them to search for various event services (like catering, photography, venues, etc.), manage bookings, and handle payments securely. The system includes features for users to browse services, providers to manage their offerings, and administrators to oversee the entire platform.

How It Works
------------
1. User Registration/Login: Users and Service Providers can create accounts.
2. Search & Discovery: Users can search for event services based on categories and locations.
3. Booking Process: Users can select a service, choose a date, and initiate a booking.
4. Payment Integration: Secure payment processing using simulated Card or UPI methods.
5. Management Dashboards: 
   - Users: View booking history and status.
   - Providers: Manage services, view incoming bookings, and generate bills.
   - Admins: Manage users, monitor all bookings, and oversee site activities.
6. Notifications: Automated email notifications for booking confirmations, invoices, and reminders (powered by OTP/JavaMailSender).

Technology Stack
----------------
- Frontend: HTML5, CSS3, JavaScript (Vanilla), Tailwind CSS, Bootstrap 5
- Backend: Java 17, Spring Boot 3.5.7
- Template Engine: Thymeleaf
- Database: MySQL
- Communication: Spring Mail (SMTP for emails)
- Build Tool: Maven

Prerequisites
-------------
- Java JDK 17 or higher
- MySQL Server (Version 8.0 recommended)
- Maven (or use the provided ./mvnw wrapper)
- A web browser (Chrome, Firefox, etc.)

How to Run the Project
----------------------
1. Database Setup:
   - Create a MySQL database named 'events_booking'.
   - Open 'src/main/resources/application.properties' and update the following with your local MySQL credentials:
     spring.datasource.username=your_username
     spring.datasource.password=your_password

2. Email Configuration (Required for OTP/Notifications):
   - The project uses Google SMTP. Open 'src/main/resources/application.properties'.
   - Update 'spring.mail.username' with your Gmail address.
   - Update 'spring.mail.password' with a Google App Password.
   - ⚠️ Important: Do NOT use your regular Gmail password. 
     Create an App Password at https://myaccount.google.com/apppasswords.
     Example App Password Format: abcd efgh ijkl mnop

3. Running the Application:
   - Open a terminal/command prompt in the project root directory.
   - Run the command:
     On Windows: mvnw spring-boot:run
     On Linux/Mac: ./mvnw spring-boot:run

4. Access the Application:
   - Once the console shows that the application has started, open your browser and visit:
     http://localhost:8080

Project Structure
-----------------
- src/main/java: Contains all Java source code (Controllers, Services, Repositories, Entities).
- src/main/resources/templates: Thymeleaf HTML templates for the UI.
- src/main/resources/static: Static assets like CSS, JS, and client-side scripts.
- src/main/resources/application.properties: Main configuration file.
- uploads/: Directory where uploaded service images are stored.

Troubleshooting
---------------
- Database Connection: Ensure MySQL service is running before starting the app.
- Email Errors: Double-check that your App Password is correct and that "Less secure app access" or "App Passwords" is enabled in your Google account.
- Port 8080: If 8080 is already in use, you can change the port in application.properties using 'server.port=XXXX'.
