# Task Timekeeper Base Code

This is the base code library for Task Timekeeper.

## This library...

 - Holds the Pojos that organize the actual timekeeping data.
 - Holds methods for common functionalities in order to provide a common, tested interface to make implementations as easy as possible.
   - Methods for CRUD operations on that data (See ActionDoer)
   - Time string parsing
   - Providing ObjectMapper for de/serialization
   
Functionality is largely split up in order to segment out pieces, so people can be selective about what they want to bring in.
