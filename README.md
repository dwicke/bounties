bounties
========
Should be refactored and possibly rearchitectured so that there is a solid core bounty hunting system and then projects can include this main bounty hunting project and build off of it.  That would make the main project less messy.  This is possible now because MASON can display g(s)etters from not just the SimState in the main view.  This needs to happen to ensure that the main code base is correct and that bugs don't get introduced.  This also mean abstracting it away from the view as well.  So, the system won't be constrained to just a grid world.

TODO:

- [X] Communication (broadcast info) // we'll just ask bondsman/grab bondsman object
- [X] Statistics 
- [X] Jumpship mechanism to take care of the different consequences.  Use the bondsman.  Tell the bondsman that you are jumping ship.  (pass yourself)
  - [x] Penalty to jumpship
  - [x] Start over if jumpship
  - [x] Once you commit then that is the bounty you get if you jumpship and come back to it.
  - [x] Can't jumpship if no one else is working on the task
- [x] Experiement scenarios like rotating the agents' home base and measuring how long they take to adapt
- [x] Learning Algorithms
  - [x] Simple algorithm
  - [x] Complex algorithm 
  - [ ] Use a leader board and then incorporate the statistics into the learning

- [x] Set up the agent's for the experiment in each of the corners

- [x] Teleport agent back to its corner once the agent has reached the task.  (considering making a delay before the task is regenerated)

