## RubberDuck

### Overview
* RubberDuck is an android application that includes various features to 
helps competitive programmers to track and improve their problem-solving 
skills.

### Development
* This project is currently under development, mainly done in the alpha2
branch. Features that have been implemented include:
  * fetching user profile data and storing in a user object which is passed
    between activities; the user object stores:
    * handle
    * url to profile photo
    * rank
    * ratings ArrayList
    * submissions ArrayList
    * verdicts HashMap
    * solved problem categories HashMap
  * renders user profile photo
  * displays ratings line graph
  * displays pie graph for submission statistics
  * displays pie graph for solved problem categories statistics
  * receives a list of all problems on codeforces

### Issues and TODOs
* Application context
  * problem suggestion implementation
  * live coding
* User interface
  * Ratings line graph
    * x-axis should contain dates 
    * add colours to the chart to convey ranks
    * each point may be tapped to link the user to the corresponding 
    contest
    * table row highlight
  * Submissions pie chart
    * Fix display of labels and numbers
    * table row highlight
  * Problem categories
    * Fix display of labels and numbers
    * table row highlight
  
  
  
  
  
  

