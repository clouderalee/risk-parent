package com.cangoonline.risk.common;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {

	private Resource addressResource;
	private String addressResourceKeyPrefix = "";
	private String portSplitChar = ":";
	private List<String> addressList;
	private Integer timeout;

	private String password;
	private Integer maxRedirections;
	private GenericObjectPoolConfig genericObjectPoolConfig;

	private JedisCluster jedisCluster;
	private Pattern portPattern;

	@Override
	public JedisCluster getObject() throws Exception {
		return jedisCluster;
	}

	@Override
	public Class<? extends JedisCluster> getObjectType() {
		return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private Set<HostAndPort> parseHostAndPort() throws Exception {

		try {
			//1.properties文件方式注入地址
			Set<HostAndPort> haps = new HashSet<HostAndPort>();
			if(addressResource != null){
				Properties prop = new Properties();
				prop.load(this.addressResource.getInputStream());
				for (Object key : prop.keySet()) {
					if (!((String) key).startsWith(addressResourceKeyPrefix)) {
						continue;
					}
					String sAddress = (String) prop.get(key);
					checkAddress(sAddress);
					String[] ipAndPort = sAddress.split(portSplitChar);
					HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
					haps.add(hap);
				}
			}

			//2.List列表方式注入地址
			if(addressList != null){
				for (String sAddress: addressList) {
					checkAddress(sAddress);
					String[] ipAndPort = sAddress.split(portSplitChar);
					HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
					haps.add(hap);
				}
			}

			return haps;
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new Exception("解析 jedis 配置文件失败", ex);
		}


	}

	private void checkAddress(String val) {
		if (!portPattern.matcher(val).matches()) {
			throw new IllegalArgumentException("Redis HostAndPort ("+val+") is illegal.");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		portPattern = Pattern.compile("^.+["+portSplitChar+"]\\d{1,5}\\s*$");
		jedisCluster = new JedisCluster(parseHostAndPort(),
				timeout, maxRedirections, genericObjectPoolConfig);
	}

	public void setAddressResource(Resource addressResource) {
		this.addressResource = addressResource;
	}

	public void setAddressResourceKeyPrefix(String addressResourceKeyPrefix) {
		this.addressResourceKeyPrefix = addressResourceKeyPrefix;
	}

	public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
		this.genericObjectPoolConfig = genericObjectPoolConfig;
	}

	public void setAddressList(List<String> addressList) {
		this.addressList = addressList;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public void setMaxRedirections(Integer maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public void setPortSplitChar(String portSplitChar) {
		this.portSplitChar = portSplitChar;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}