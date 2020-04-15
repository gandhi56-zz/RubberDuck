## RubberDuck

### Overview
* RubberDuck is an android application that includes various features to helps competitive programmers to track and improve their problem-solving skills. Some features include:
  * sync with codeforces profile
  * play code to survive
    * solve problems out of surprise based on your skill level until you give up!
  * solve suggested problems from codeforces
  * track and visualize performance on a website and multiple websites together
  * a blog containing articles on algorithms and data structures

### Development
* This project is currenly under development, mainly done in the bottombar branch. 
  * Features that have been implemented includes:
    * Login activity (MainActivity.xml)
    * Firebase database setup

### Issues
* UI: the bottombar navigation controls and their corresponding pages
  * Stats
    * display user performance for codeforces including ratings graph and solved problems graph respecting difficulty
  * Contest
    * view problems synchronized submission status during contest
  * Play
    * loads a page where the user can select a training platform
      * warmup: RubberDuck recommends problems to warm the user up
      * code to survive: a rapid fire game where user is spontaneously given problems to solve with a survival time limit
      * master it: user will be given problems of a particular kind at random to measure performance in specific classes of problems
  * Blog
    * display articles on algorithms and data structures
  * User
    * user profile settings
* implement HTTP GET request method to fetch data from the codeforces API and store the response in a JSONObject
* implement UI components to appropriately display JSONObjects
