# Ration Ledger Digitizer

A Java Full Stack / Jakarta Servlet + JSP + JDBC + MySQL application for digital ration record management.

## Stack
- JDK 21
- Apache Tomcat 10.x
- Jakarta Servlets 6
- JSP/JSTL
- JDBC
- MySQL 8+
- HTML5, CSS3, JavaScript
- Maven WAR project (also importable into Eclipse as Existing Maven Project)

## Implemented modules
### Admin
- Admin login
- Dashboard statistics
- Beneficiary CRUD + search/filter
- Monthly ration allocation
- Distribution schedule
- Ration distribution / collection
- Duplicate collection prevention
- Announcements with important flag
- Stock management
- Monthly reports and CSV export
- Audit log
- Password change
- Session/role protection

### User
- User login
- Personal dashboard
- Current month ration allocation
- Distribution schedule
- Collection status
- Ration history
- Announcements
- Profile update
- Digital receipt view

## Setup
1. Install MySQL 8+ and JDK 21.
2. Create the database:
   `mysql -u root -p < database/ration_ledger.sql`
3. Edit `src/main/java/com/rationledger/util/DBConnection.java` and set MySQL username/password.
4. In Eclipse: File -> Import -> Existing Maven Projects -> select this folder.
5. Add Apache Tomcat 10.x runtime to the project.
6. Run on Server.
7. Open:
   `http://localhost:8080/RationLedgerDigitizer/`
8. Demo admin:
   username: `admin`
   password: `admin123`
9. Demo user:
   username: `user1`
   password: `user123`

Passwords are SHA-256 hashed in the database. Change demo credentials before real use.


## Fix for HTTP 404 at `/login`
The fixed version explicitly maps LoginServlet, LogoutServlet, AdminServlet and UserServlet in `WEB-INF/web.xml`. It also explicitly maps the authentication filter.

If Eclipse still serves an old deployment:
1. Stop Tomcat.
2. Project -> Clean.
3. Right-click the project -> Maven -> Update Project.
4. In Servers, remove the old project from Tomcat.
5. Clean the Tomcat server.
6. Add the project again and Run on Server.
7. Open `http://localhost:8080/RationLedgerDigitizer/`.


## Login and registration
- `/index.jsp` has separate Beneficiary and Administrator login modes.
- `/signup.jsp` registers a beneficiary and creates the matching user account in one database transaction.
- Public self-registration is for beneficiaries only. Admin access must not be self-assigned; use the seeded admin account or create admins through a trusted database/admin process.
- Demo credentials: `admin / admin123`, `user1 / user123`.
