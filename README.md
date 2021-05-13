# Data Manager

Did you ever need to save or load some data,
but did not want to bother with creating a system for it?

Well fear not! This project has got you covered!

NOTE: This project was made in 30 minutes and is not yet complete, it might work but its not guaranteed.

## Features

- Saved data will be converted to instructions which are easy to read.
- Allows for fast and easy storage.
- You don't have to bother with errors as they are handled by the library.
- Best of all, it is completely free!


## Why?

- To make your life that tiny bit more easy, and allow you to focus
on whatever you need to do.
  
## Plans

- Well, none currently! I guess only improving this
in terms of efficiency and stability
  
- Probably add some features if requested.

## Where can I use this?

- Everywhere! You can edit it for personal use, and if you add a feature you think is good, please send a Pull Request

## Usage
- Go to the [Releases Section](https://github.com/IkeVoodoo/DataManager/releases) and download the latest JAR file
- Maven/Gradle coming soon TM

## Examples
Comments
```
# This is a comment
# I work even inside a instruction!
# You can also add a \ behind me to exclude this a as a comment!
```

<br></br>
Instructions
```
# Start a fragment with an id and a name.
# The id is required, name is not.
>FRAG <id> [name]
str: Hey!!! # String
int: 40 # Integers

# Booleans are special, as if you give them "true"
# they will represent true, otherwise any other input is false
bool: true
# End a fragment
>END
```
  
## Developers

- IkeVoodoo

Shh, there is only me for now!
