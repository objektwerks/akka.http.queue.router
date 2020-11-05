Akka-Http Queue Router
----------------------
>This prototype does not use a single queue connection nor a pool of queue connections.
Instead, it creates a new queue connection just-in-time in anticipation of targeting a
RabbitMQ cluster. Read these posts for more insights:

* http://stackoverflow.com/questions/10407760/is-there-a-performance-difference-between-pooling-connections-or-channels-in-rab
* https://www.rabbitmq.com/blog/2011/09/24/sizing-your-rabbits/

>Queues are accessed dynamically via a post request containing an id. The id is prefixed
just-in-time to a default exchange name, queue name and routing key. Then a queue connection
is created, accessed and closed.

Install
-------
1. brew install RabbitMQ

RabbmitMQ
---------
1. brew services start rabbitmq
2. brew services stop rabbitmq

Test
----
1. sbt clean it:test
>**View** the RabbitMQ Web UI at: http://localhost:15672/  [ user: guest, password: guest ]

RabbitMqCtl
-----------
>See https://www.rabbitmq.com/man/rabbitmqctl.1.man.html

1. List Queues
   * rabbitmqctl list_queues [ name messages_ready messages_unacknowledged ]
2. Restart
   * rabbitmqctl stop_app
   * rabbitmqctl reset
   * rabbitmqctl start_app

Pack
----
1. sbt clean compile pack

Run
---
1. sbt run

Execute
-------
1. ./target/pack/bin/queue-router-app