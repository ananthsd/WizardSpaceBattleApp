# Wizard Space Battle(Android SDK 16+)
This is the android app version of Wizard Space Battle. The desktop version is also written in Java. You can find it [here](https://github.com/Thedarkbobman/WizardSpaceBattle).

## What is this?
Two people play on the same device, and each player controls a wizard that shoots a laser.

The laser can collide with the player and the other player's laser. When a player loses all their health, the round finishes, a new one starts, and the other player gains a point. 

The controls are joystick based with a dynamic center that changes based on the first finger input. Basically the first finger down on a player's side becomes the center of the joystick, and the distance from the point determines their speed. There is a max speed put into place, so trying to make the joystick center to edge distance as large as possible doesn't give you an advantage. The controls are pretty sensitive now, and all of these values are subject to change. 

## What did I use?
I used OpenGL ES 2.0 to do the graphics, because I felt that using the canvas would lead to large memory usage. This worked out because I had to perform very little optimization to get the game working smoothly on my device(60fps). I chose 2.0 specifically because it supports SDK versions 8+. I chose not to use 3.0 because I figured there was more documentation on 2.0, the differences between 2.0 and 3.0 didn't matter for my purposes, and I could reach a broader target audience. 

