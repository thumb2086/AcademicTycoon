### v0.4: Navigation & Gradle Fix

- **UI & Navigation:** Implemented a full bottom navigation bar using Jetpack Compose Navigation, connecting the four main screens of the app.
- **Build & Dependencies:** Corrected critical errors in the Gradle configuration by adding missing dependencies for Hilt, Room, Retrofit, and Navigation. Successfully synchronized the project.

### v0.3: UI Foundation & Theming

- **UI:** Established the basic UI structure in `MainActivity` using Jetpack Compose, including a `TopAppBar` to display player balance and debt.
- **Theming:** Implemented the "Industrial Black" (#121212) and "Fluorescent Green" (#00FF00) color scheme as defined in the project plan.
- **Visuals:** Added a blinking red animation for the debt display to alert the user of their financial status, enhancing the "high-pressure" theme.

### v0.2: Network Sync & Dependency Injection

- **Networking:** Implemented `ApiService` with Retrofit to fetch the question bundle from a remote GitHub repository.
- **Repository:** Created `QuestionRepository` to manage data synchronization between the network and local Room database, including offline support.
- **Architecture:** Set up Hilt for dependency injection, providing a scalable and maintainable structure for the app.

### v0.1: Initial Project Setup

- **Core Logic:** Established Room database schema for `UserProfile` and `Question` entities.
- **Architecture:** Implemented `FinanceViewModel` with initial 80/20 debt repayment logic.
- **Data Layer:** Created `AppDatabase`, DAOs, and `UserRepository` as a foundation for data management.
