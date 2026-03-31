# Teacher Assistant - Android App

A modern Android client for the [LocalAttendance Teacher Assistant](https://github.com/richiesamlie/LocalAttendace-Final) web application. This app connects to the backend API and provides all the classroom management features on your Android device.

## Features

### Core Functionality
- **Take Attendance** - Record daily attendance with Present/Absent/Sick/Late status
- **Student Roster** - Add, edit, archive students with parent contact info
- **Timetable** - Manage weekly class schedules with subjects and lessons
- **Events** - Create and manage classwork, tests, exams, and holidays
- **Seating Chart** - Visual 5x6 grid for drag-and-drop seat assignments
- **Monthly Reports** - View attendance statistics with progress indicators
- **Random Picker** - Animated random student selection tool

### Multi-Teacher Support
- Google Classroom-style class management
- Invite co-teachers to shared classes
- Role-based access (Owner vs Teacher)

### Authentication
- Secure JWT cookie-based authentication
- Login with username/password
- Persistent sessions

## Screenshots

| Login | Dashboard | Attendance |
|-------|-----------|------------|
| Login with credentials | Quick stats & class selection | Record daily attendance |

| Student Roster | Timetable | Seating Chart |
|----------------|-----------|---------------|
| Manage students | Weekly schedule | Visual seat layout |

| Reports | Random Picker | Settings |
|---------|---------------|----------|
| Monthly statistics | Animated selection | Class management |

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** or higher
- **Android SDK** with API level 26+ (Android 8.0)
- **Teacher Assistant Backend** running locally or on network

## Backend Setup

This app requires the [Teacher Assistant Backend](https://github.com/richiesamlie/LocalAttendace-Final) to be running.

### Quick Start (Backend)

```bash
# Clone the backend
git clone https://github.com/richiesamlie/LocalAttendace-Final.git
cd LocalAttendace-Final

# Install dependencies
npm install

# Set environment
cp .env.example .env
# Edit .env and set JWT_SECRET

# Start server
npm run dev
```

The backend will run at `http://localhost:3000`

## Installation

### Option 1: Clone and Build

```bash
# Clone this repository
git clone https://github.com/yourusername/TeacherAssistant-Android.git
cd TeacherAssistant-Android

# Open in Android Studio
# File > Open > Select the project folder

# Sync Gradle files
# Click "Sync Now" when prompted

# Run on emulator or device
# Click the Run button (green play icon)
```

### Option 2: Download APK

Download the latest APK from the [Releases](https://github.com/yourusername/TeacherAssistant-Android/releases) page.

## Configuration

### Backend URL

By default, the app connects to the backend at `http://10.0.2.2:3000/api/` (Android emulator localhost).

**For Physical Devices:**

1. Find your computer's IP address:
   ```bash
   # Windows
   ipconfig
   
   # Mac/Linux
   ifconfig
   ```

2. Update the `BASE_URL` in `app/src/main/java/com/localattendance/teacherassistant/data/api/ApiClient.kt`:
   ```kotlin
   const val BASE_URL = "http://YOUR_IP:3000/api/"
   ```

3. Start the backend with network access:
   ```bash
   npm run dev:network
   ```

### Default Credentials

- **Username:** `admin`
- **Password:** `teacher123`

> ⚠️ Change the default password in production!

## Project Architecture

```
app/src/main/java/com/localattendance/teacherassistant/
├── data/
│   ├── api/           # Retrofit API client and service
│   ├── model/         # Data classes (Teacher, Student, Class, etc.)
│   └── repository/    # Repository pattern for data access
├── ui/
│   ├── navigation/    # Navigation setup
│   ├── screen/        # Compose UI screens
│   ├── viewmodel/     # ViewModels for state management
│   └── theme/         # Material 3 theme configuration
├── MainActivity.kt    # Entry point
└── TeacherAssistantApp.kt  # Application class
```

### Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Design System | Material 3 |
| Networking | Retrofit + OkHttp |
| JSON Parsing | Gson |
| Architecture | MVVM |
| State Management | StateFlow |
| Navigation | Navigation Compose |

## API Endpoints

The app connects to 31 REST API endpoints:

### Authentication
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout
- `GET /auth/verify` - Verify session
- `GET /auth/me` - Get current user

### Classes
- `GET /classes` - List classes
- `POST /classes` - Create class
- `PUT /classes/:id` - Update class
- `DELETE /classes/:id` - Delete class

### Students
- `GET /classes/:id/students` - List students
- `POST /classes/:id/students` - Add student
- `PUT /students/:id` - Update student
- `DELETE /students/:id` - Archive student

### Attendance
- `GET /classes/:id/records` - Get records
- `POST /records` - Save attendance

### Events
- `GET /classes/:id/events` - List events
- `POST /classes/:id/events` - Create event
- `PUT /events/:id` - Update event
- `DELETE /events/:id` - Delete event

### Timetable
- `GET /classes/:id/timetable` - Get timetable
- `POST /classes/:id/timetable` - Add slot
- `PUT /timetable/:id` - Update slot
- `DELETE /timetable/:id` - Delete slot

### Seating
- `GET /classes/:id/seating` - Get layout
- `POST /classes/:id/seating` - Update seat
- `PUT /classes/:id/seating` - Replace layout
- `DELETE /classes/:id/seating` - Clear all

## Development

### Building for Release

```bash
# Generate signed APK
# Build > Generate Signed Bundle / APK

# Or via command line
./gradlew assembleRelease
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

### Code Style

The project follows Kotlin coding conventions. Run lint checks:

```bash
./gradlew lint
```

## Troubleshooting

### Connection Refused
- Ensure the backend server is running
- Check the `BASE_URL` configuration
- For physical devices, use your computer's IP address instead of `10.0.2.2`

### Login Failed
- Verify backend is accessible
- Check default credentials (`admin` / `teacher123`)
- Clear app data and try again

### Build Errors
- Ensure JDK 17 is installed
- Sync Gradle files
- Clean and rebuild: `Build > Clean Project` then `Build > Rebuild Project`

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is for educational and personal use.

## Credits

- Backend: [LocalAttendance-Final](https://github.com/richiesamlie/LocalAttendace-Final)
- UI Framework: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Design: [Material 3](https://m3.material.io/)

## Support

For issues and questions:
- Open an issue on GitHub
- Check the [User Guide](https://github.com/richiesamlie/LocalAttendace-Final/blob/main/USER_GUIDE.md) for backend documentation

---

**Note:** This is a client application. The backend server must be running for the app to function.
