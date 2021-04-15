# Check-in-1
* I chose to implement the firebase db complexity
  * A user can create a new account and use it to sign into the app
  * The sign-up screen ensures passwords match and account does not already exist
    * Later I'll make sure passwords are strong
  * Log-in screen logs you in if email and password are correct
  * I'll use FB to store a user's recent restaurant choices and custom preferences later
    * setup the recyclerView for the recent picks fragment
* I also set up a large part of the home screen fragment
  * Only UI components are set up

# Check-in-2
* I chose to make significant progress towards completing the home fragment and results fragment
 * The UI components of the home fragment now affect the search query for the Yelp API call
  * I will get the user's location next check in. For now it's set to D.C. by default
 * The results fragment dynamically shows restaurants based on the user's search query
  * A user can also favorite a restaurant by tapping the star icon in the top right (animation is a work in progress)
   * Currently the FB database updates when a user is added and they add restaurants to their favorites list
    * Next check in will make sure favorited restaurants for a user are added to the favorites fragment
