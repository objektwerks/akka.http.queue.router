Rest Queue Router
--------------------------
>Rest queue router prototype

Install
-------
1. brew install RabbitMQ

Start
-----
1. brew services start rabbitmq

Stop
----
1. brew services stop rabbitmq

Test
----
1. sbt clean it:test

Pack
----
1. sbt clean compile it:test pack

Config
------
> The following configuration file:

Run
---
>Run ValidatorResponseService via sbt:

1. sbt run

>Run RestQueueRouterApp via pack:

1. ./target/pack/bin/queue-router-app

>RestQueueRouterApp loads **app.validator.response.service.conf** from the current working directory.

Log
---
>The app log is written to: ./log/app.queue.router.log

RabbitMQ Admin
--------------
>See rabbitmqadmin @ https://www.rabbitmq.com/management-cli.html

RabbitMQ Control
----------------
>See rabbitmqctl @ https://www.rabbitmq.com/man/rabbitmqctl.1.man.html

>List

1. rabbitmqctl list_queues name messages_ready messages_unacknowledged

>Reset

1. rabbitmqctl stop_app
2. rabbitmqctl reset
3. rabbitmqctl start_app
