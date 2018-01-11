package com.cangoonline.risk.common;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2017\12\13 0013.
 */
public class JedisFactory extends JedisPool{
    public JedisFactory(GenericObjectPoolConfig jedisPoolConfig, String host, int port, int timeout, String password) {
        super(jedisPoolConfig, host, port, timeout, password, 0);
    }
}
