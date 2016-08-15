package rest.queue.router

object QueueConnectorConf {
  // Ficus does not like competing apply methods!
  def copy(id: String, conf: QueueConnectorConf): QueueConnectorConf = {
    val exchangeName = id + conf.exchangeName
    val queueName = id + conf.queueName
    val routingKey = id + conf.routingKey
    conf.copy(exchangeName = exchangeName, queueName = queueName, routingKey = routingKey)
  }
}

case class QueueConnectorConf(url: String,
                              exchangeName: String,
                              exchangeType: String,
                              queueName: String,
                              isQueueDurable: Boolean,
                              routingKey: String,
                              autoAck: Boolean,
                              publishConfirmationTimeout: Int)