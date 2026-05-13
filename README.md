# WhereToNext
WhereToNext is a Java Swing app that helps you explore cities you would like to travel to. It shows top hotels, restaurants, and activities using the Yelp API, lets you view details for each business, and add them to a personal itinerary.

## Features
- Search for a city and see top businesses.  
- View details like name, address, rating, reviews, and image.  
- Add businesses to your itinerary.  
- Navigate easily between search results, business details, and itinerary.  
- Simple and clean GUI with background images and tabs.

## Getting Started
**Step 1 — Clone the repository:** git@github.com:NMSU-CS-CS371/team-project-real-wheretonext.git
**Step 2 — Navigate into the project folder:** cd team-project-real-wheretonext
**Step 3 — Compile the project:** make
**Step 4 — Run the project:** make run
**To clean compiled files:** make clean

## Dependencies

- Java 11 or higher
- [Gson 2.10.1](https://github.com/google/gson) — included as `gson-2.10.1.jar` in the project root

## Project Structure

- `WhereToNextUI.java` — main window and entry point
- `ResultsPanel.java` — displays search results in tabs
- `SearchController.java` — handles search logic and Yelp API calls
- `YelpApiClient.java` — connects to the Yelp API
- `BusinessDetailsPage.java` — shows detailed info for a selected business
- `BackgroundPanel.java` — custom panel for background images
- `itinerary.dat` — saved itinerary data (created automatically on first use)
- `images/` — background images used in the UI
