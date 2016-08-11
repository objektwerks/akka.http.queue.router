package rest.queue.router

case class QueueConnectorConf(url: String,
                              exchangeName: String,
                              exchangeType: String,
                              queueName: String,
                              isQueueDurable: Boolean,
                              routingKey: String,
                              autoAck: Boolean,
                              publishConfirmationTimeout: Int)