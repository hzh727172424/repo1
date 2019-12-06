package com.leyou.search.mq;


import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {
    @Autowired
     private SearchService searchService;
    @RabbitListener(bindings = @QueueBinding(value=@Queue(name = "search.item.insert.queue",durable = "true"),
            exchange=@Exchange(name ="ly.item.exchange",type = ExchangeTypes.TOPIC)
    ,key ={"item.insert","item.update"}))
    public void ListenerInsertOrUpdate(Long spuId){
     if (spuId==null){
         return;
     }
     searchService.createInsertOrUpdate(spuId);
    }
    @RabbitListener(bindings = @QueueBinding(value=@Queue(name = "search.item.delete.queue",durable = "true"),
            exchange=@Exchange(name ="ly.item.exchange",type = ExchangeTypes.TOPIC)
            ,key ="item.delete"))
    public void ListenerDelete(Long spuId){
        if (spuId==null){
            return;
        }
    searchService.deleteIndex(spuId);
    }
}
