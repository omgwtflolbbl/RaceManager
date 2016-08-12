# RaceManager
This branch is for working on changes with MultiGP.

This project has a few goals:

-Build a scraper for MultiGP to pull information about races and racers

-Quickly build a server and DB to pull/store data about these races and racers

-Create mobile apps to organize and share these races and provide realtime notifications regarding race information and scheduling (because we're god awful about that)

-Give organizers the ability to track and score racers straight from the app and upload the results to multiGP

The server will be built up with Flask and will use Firebase for a RTDB and notifications. No particular reason behind choosing these technologies other than "let's learn something about NoSQL". At some point the scraper may become more of a full API (scoring, check ins, discus integration) but that's a low priority.

Android will come first, iOS afterwards.
