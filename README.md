Rest Queue Router
--------------------------
>This prototype does not use a single queue connection nor a pool of queue connections.
Instead, it creates a new queue connection just-in-time in anticipation of targeting a
RabbitMQ cluster. Read these posts for more insights:

* http://stackoverflow.com/questions/10407760/is-there-a-performance-difference-between-pooling-connections-or-channels-in-rab
* https://www.rabbitmq.com/blog/2011/09/24/sizing-your-rabbits/

>Queues are accessed dynamically via a post request containing an id. The id is prefixed
just-in-time to the exchange name, queue name and routing key.

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
>Run QueueRouterApp via sbt:

1. sbt run

>Run QueueRouterApp via pack:

1. ./target/pack/bin/queue-router-app

Logs
----
1. test log: ./target/it.test.log.txt
2. app log: ./log/app.queue.router.log

RabbitMQ
--------
>See rabbitmqadmin @ https://www.rabbitmq.com/management-cli.html

>See rabbitmqctl @ https://www.rabbitmq.com/man/rabbitmqctl.1.man.html

>List

1. rabbitmqctl list_queues name messages_ready messages_unacknowledged

>Reset

1. rabbitmqctl stop_app
2. rabbitmqctl reset
3. rabbitmqctl start_app