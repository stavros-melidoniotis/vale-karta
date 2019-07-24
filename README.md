***Vale Karta! Application***

Vale Karta! is an Android application developed for personal use. It uses a foreground, never ending service that listens for incoming SMS messages through a broadcast receiver.
When a new SMS arrives, the receiver starts a second service that checks whether the sender is WhatsUP or not. If so, then it parses the SMS body to save the date that user's 
package ends and creates a calendar event with a reminder one day prior to that date. This application notifies the user with two status bar notifications. One for when an event 
has been successfully created and the other for when the SMS that was received wasn't from WhatsUP.


***Knowledge Aquired***

*  Using foreground services
*  Displaying interactive status bar notifications
*  Using Broadcast Receivers
*  Requiring multiple runtime permissions
*  Reading device's SMS messages
*  Programmatically adding calendar event and reminders
