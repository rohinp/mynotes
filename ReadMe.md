# My Note App

 It is a simple note app exercise. 
 The intention is to show how easy it is to use tagless-final style  and implement business as well as NFRs (log, metric, etc)
 (It is still a WIP)
 
 ### Prerequisites
 1. sbt
 2. docker
 
 ### what you need to run the app
 1. Get a docker image for mongo https://hub.docker.com/_/mongo
 2. Get a docker image for influxDB https://hub.docker.com/_/influxdb
 
 ### Project structure (WIP high possibility of change)
 
 ```$xslt
.
├── main
│   └── scala
│       ├── app
│       │   └── Code to start the application
│       ├── core
│       │   ├── domain
│       │   │   ├── ADT's and algebra declared here
│       │   └── service
│       │       ├── This is where DSL is actually used to do some business
│       ├── dsl
│       │   ├── Multiple implementation of the DSL can be defined here
│       │   └── interpreter
│       │       ├── inmemory
│       │       │   ├── In memory implementation of the DSL
│       │       └── persistent
│       │       │   ├── persistent implementation with mongo and influxdb
│       └── repo
│           ├── inmemory
│           │   └── repo for in memory code
│           └── persistent
│               └── repo for mongo and influx DB code
└── test
    └── scala
        └── core
            └── service
                └── Tests are purely based on inmemory implementation
```

### How to test and run
```sbtshell
sbt test

sbt run
```