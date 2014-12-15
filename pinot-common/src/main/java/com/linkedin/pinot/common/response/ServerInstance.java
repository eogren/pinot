package com.linkedin.pinot.common.response;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service abstraction.
 * A service is identified by its hostname and port.
 * Internally, an ip address is also resolved.
 * 
 * Nuances:
 * -------
 * A hostname "localhost" will not be resolved to the
 * local hostname and will retain the hostname as "localhost".
 * If the name passed is "127.0.0.1", then it is resolved to the
 * local hostname.
 */
public class ServerInstance {
  protected static Logger LOG = LoggerFactory.getLogger(ServerInstance.class);

  public static final String NAME_PORT_DELIMITER = ":";

  /** Host-name where the service is running **/
  private final String _hostname;

  /** Service Port **/
  private final int _port;

  /** IP Address. Not used in equals/hash-code generation **/
  private final InetAddress _ipAddress;

  /**
   * Use this constructor if the name and port are embedded as string with ":" as delimiter
   *
   * @param namePortStr Name and Port settings
   */
  public ServerInstance(String namePortStr) {
    this(namePortStr.split(NAME_PORT_DELIMITER)[0], Integer.parseInt(namePortStr.split(NAME_PORT_DELIMITER)[1]));
  }

  public ServerInstance(String name, int port) {
    super();
    InetAddress ipAddr = null;
    try {
      ipAddr = InetAddress.getByName(name);
    } catch (UnknownHostException e) {
      LOG.error("Unable to fetch IpAddresses for host:" + name, e);
      ipAddr = null;
    }

    _ipAddress = ipAddr;
    _hostname = _ipAddress != null ? _ipAddress.getHostName() : name;
    _port = port;
  }

  public String getHostname() {
    return _hostname;
  }

  public int getPort() {
    return _port;
  }

  public InetAddress getIpAddress() {
    return _ipAddress;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + (_hostname == null ? 0 : _hostname.hashCode());
    result = (prime * result) + _port;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ServerInstance other = (ServerInstance) obj;
    if (_hostname == null) {
      if (other._hostname != null) {
        return false;
      }
    } else if (!_hostname.equals(other._hostname)) {
      return false;
    }
    if (_port != other._port) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ServerInstance [_hostname=" + _hostname + ", _port=" + _port + ", _ipAddress=" + _ipAddress + "]";
  }

}
