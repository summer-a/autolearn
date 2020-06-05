package com.autolearn.icve.utils;

import com.sun.management.OperatingSystemMXBean;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;

/**
 * 获取服务器系统信息
 */
public class SystemInfoUtil implements Serializable {
    /**
     * 系统名
     */
    private String osName;
    /**
     * 系统架构
     */
    private String osArch;
    /**
     * 系统版本号
     */
    private String osVersion;
    /**
     * 系统IP
     */
    private String osIp;
    /**
     * 系统MAC地址
     */
    private String osMac;
    /**
     * 系统时间
     */
    private String osDate;
    /**
     * 系统CPU个数
     */
    private Integer osCpus;
    /**
     * 系统用户名
     */
    private String osUserName;
    /**
     * 用户的当前工作目录
     */
    private String osUserDir;
    /**
     * 用户的主目录
     */
    private String osUserHome;
    /**
     * Java的运行环境版本
     */
    private String javaVersion;
    /**
     * java默认的临时文件路径
     */
    private String javaIOTmpdir;
    /**
     * java 平台
     */
    private String sunDesktop;
    /**
     * 文件分隔符 在 unix 系统中是"/"
     */
    private String fileSeparator;
    /**
     * 路径分隔符 在 unix 系统中是":"
     */
    private String pathSeparator;
    /**
     * 行分隔符 在 unix 系统中是"/n"
     */
    private String lineSeparator;
    /**
     * 服务context
     **/
    private String serverContext;
    /**
     * 服务器名
     */
    private String serverName;
    /**
     * 服务器端口
     */
    private Integer serverPort;
    /**
     * 服务器地址
     */
    private String serverAddr;
    /**
     * 获得客户端电脑的名字,若失败,则返回客户端电脑的ip地址
     */
    private String serverHost;
    /**
     * 服务协议
     */
    private String serverProtocol;

    /**
     * Java虚拟机内存总量
     */
    private long totalMemory;

    /**
     * 获取Java虚拟机空闲内存量
     */
    private long freeMemory;

    /**
     * 获取Java虚拟机试图使用的最大内存量
     */
    private long maxMemory;

    /**
     * 服务器系统内存总量/MB
     */
    private long osTotalMemory;

    /**
     * 服务器系统空闲内存/MB
     */
    private long osFreeMemory;


    public static SystemInfoUtil SYSTEM_INFO = new SystemInfoUtil();

    public static SystemInfoUtil getInstance() {
        return SYSTEM_INFO;
    }

    public static SystemInfoUtil getInstance(HttpServletRequest request) {
        SYSTEM_INFO.ServerInfo(request);
        return SYSTEM_INFO;
    }

    public SystemInfoUtil() {
        super();
        init();
    }

    public SystemInfoUtil(HttpServletRequest request) {
        super();
        init();
        /** 额外信息 */
        this.ServerInfo(request);
    }


    /**
     * 初始化基本属性
     */
    private void init() {
        Properties props = System.getProperties();
        this.javaVersion = props.getProperty("java.version");
        this.javaIOTmpdir = props.getProperty("java.io.tmpdir");
        this.osName = props.getProperty("os.name");
        this.osArch = props.getProperty("os.arch");
        this.osVersion = props.getProperty("os.version");
        this.fileSeparator = props.getProperty("file.separator");
        this.pathSeparator = props.getProperty("path.separator");
        this.lineSeparator = props.getProperty("line.separator");
        this.osUserName = props.getProperty("user.name");
        this.osUserHome = props.getProperty("user.home");
        this.osUserDir = props.getProperty("user.dir");
        this.sunDesktop = props.getProperty("sun.desktop");
        this.osDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.osCpus = Runtime.getRuntime().availableProcessors();

        // 获取内存信息
        getMemory();

        try {
            ipMac();
        } catch (Exception e) {
            this.osIp = "";
            this.osMac = "";
        }
    }

    /**
     * 获取ip和mac地址 * @throws Exception
     */
    @SuppressWarnings("resource")
    private void ipMac() throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        NetworkInterface ni = NetworkInterface.getByInetAddress(address);
        ni.getInetAddresses().nextElement().getAddress();
        byte[] mac = ni.getHardwareAddress();
        String sIP = address.getHostAddress();
        String sMAC = "";
        Formatter formatter = new Formatter();
        for (int i = 0; i < mac.length; i++) {
            sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "").toString();
        }
        this.osIp = sIP;
        this.osMac = sMAC;
    }

    /**
     * 获取系统和java的内存用量信息
     */
    private void getMemory() {
        // 获取Java虚拟机内存总量
        this.totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;

        // 获取Java虚拟机空闲内存量
        this.freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;

        // 获取Java虚拟机试图使用的最大内存量
        this.maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;

        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 服务器系统内存总量/MB
        this.osTotalMemory = osmb.getTotalPhysicalMemorySize() / 1024 / 1024;

        // 服务器系统空闲内存/MB
        this.osFreeMemory = osmb.getFreePhysicalMemorySize() / 1024 / 1024;
    }

    /**
     * 获取服务器信息
     * @param request
     */
    public void ServerInfo(HttpServletRequest request) {
        this.serverName = request.getServerName();
        this.serverPort = request.getServerPort();
        Object attribute = request.getAttribute("X-Forwarded-For");
        this.serverAddr = attribute == null ? "" : attribute.toString();
        this.serverHost = request.getRemoteHost();
        this.serverProtocol = request.getProtocol();
        this.serverContext = request.getContextPath();
    }

    @Override
    public String toString() {
        return "SystemInfoUtils{" +
                "osName='" + osName + '\'' +
                ", osArch='" + osArch + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", osIp='" + osIp + '\'' +
                ", osMac='" + osMac + '\'' +
                ", osDate=" + osDate +
                ", osCpus=" + osCpus +
                ", osUserName='" + osUserName + '\'' +
                ", osUserDir='" + osUserDir + '\'' +
                ", osUserHome='" + osUserHome + '\'' +
                ", javaVersion='" + javaVersion + '\'' +
                ", javaIOTmpdir='" + javaIOTmpdir + '\'' +
                ", sunDesktop='" + sunDesktop + '\'' +
                ", fileSeparator='" + fileSeparator + '\'' +
                ", pathSeparator='" + pathSeparator + '\'' +
                ", lineSeparator='" + lineSeparator + '\'' +
                ", serverContext='" + serverContext + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverPort=" + serverPort +
                ", serverAddr='" + serverAddr + '\'' +
                ", serverHost='" + serverHost + '\'' +
                ", serverProtocol='" + serverProtocol + '\'' +
                ", totalMemory=" + totalMemory +
                ", freeMemory=" + freeMemory +
                ", maxMemory=" + maxMemory +
                ", osTotalMemory=" + osTotalMemory +
                ", osFreeMemory=" + osFreeMemory +
                '}';
    }

    public String getOsName() {
        return osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsIp() {
        return osIp;
    }

    public String getOsMac() {
        return osMac;
    }

    public String getOsDate() {
        return osDate;
    }

    public Integer getOsCpus() {
        return osCpus;
    }

    public String getOsUserName() {
        return osUserName;
    }

    public String getOsUserDir() {
        return osUserDir;
    }

    public String getOsUserHome() {
        return osUserHome;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getJavaIOTmpdir() {
        return javaIOTmpdir;
    }

    public String getSunDesktop() {
        return sunDesktop;
    }

    public String getFileSeparator() {
        return fileSeparator;
    }

    public String getPathSeparator() {
        return pathSeparator;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public String getServerContext() {
        return serverContext;
    }

    public String getServerName() {
        return serverName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getServerProtocol() {
        return serverProtocol;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getOsTotalMemory() {
        return osTotalMemory;
    }

    public long getOsFreeMemory() {
        return osFreeMemory;
    }
}