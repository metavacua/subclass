# Maven Proxy Configuration

## Problem

This project builds in a container environment with **no local DNS** and **all traffic routed through an authenticated HTTP/HTTPS proxy**. Maven's plugin resolution fails with DNS resolution errors even though:
- `curl` and other tools work fine through the proxy
- `JAVA_TOOL_OPTIONS` contains proxy settings
- Network connectivity is functional

## Root Cause

Maven has **two separate networking subsystems** that handle proxies differently:

| Phase | Component | Resolver |
|-------|-----------|----------|
| **Plugin Resolution** | Maven core + Plexus/Aether | Uses Maven's own proxy config (settings.xml) |
| **Artifact Resolution** | Maven Wagon | Uses both settings.xml and JAVA_TOOL_OPTIONS |

**The Issue**: Maven's **plugin resolution phase** occurs before settings.xml proxy configuration is fully applied. Maven tries to resolve `maven-clean-plugin` and other core plugins using DNS directly, which fails in a container with empty `/etc/resolv.conf`.

## Solution

Configure Maven's `~/.m2/settings.xml` with explicit proxy settings **before running any Maven commands**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <proxies>
    <proxy>
      <id>container-proxy</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>21.0.1.75</host>
      <port>15004</port>
      <username>YOUR_PROXY_USER</username>
      <password>YOUR_PROXY_PASSWORD</password>
      <nonProxyHosts>localhost|127.0.0.1|*.local</nonProxyHosts>
    </proxy>
    <proxy>
      <id>container-proxy-http</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>21.0.1.75</host>
      <port>15004</port>
      <username>YOUR_PROXY_USER</username>
      <password>YOUR_PROXY_PASSWORD</password>
      <nonProxyHosts>localhost|127.0.0.1|*.local</nonProxyHosts>
    </proxy>
  </proxies>
</settings>
```

## Why This Works

1. **Explicit Configuration**: Maven reads settings.xml early in initialization before attempting plugin resolution
2. **Two Protocols**: Some Maven operations use HTTP, others HTTPS - both must be configured
3. **Authentication**: JWT tokens (or basic auth) must be explicitly provided to the proxy
4. **DNS Handled by Proxy**: The proxy at `21.0.1.75:15004` performs DNS resolution internally; Maven never attempts local DNS

## Diagnostic Commands

Test whether Maven's proxy is correctly configured:

```bash
# Check effective Maven settings (includes proxy configuration)
mvn help:effective-settings -X | grep -A 5 "<proxy>"

# Verify Maven can reach Maven Central
mvn help:describe -Dplugin=org.apache.maven.plugins:maven-help-plugin -X
```

## Environment Variables

The container provides these via `GLOBAL_AGENT_HTTP_PROXY` and `YARN_HTTP_PROXY`:
- **Proxy Host**: `21.0.1.75` (or `21.0.1.35` depending on session)
- **Proxy Port**: `15004`
- **Username**: `container_container_[UUID]--claude_code_remote--[SUFFIX]`
- **Password**: JWT token (long alphanumeric string)

Extract credentials from environment and populate `~/.m2/settings.xml`.

## Alternative: Offline Build

If proxy configuration fails, build offline using cached dependencies:

```bash
mvn clean compile -o
```

This requires that `/home/user/.m2/repository/` contains cached Maven plugins and dependencies.

## References

- [Maven Settings Reference](https://maven.apache.org/settings.html)
- [Maven Proxy Configuration](https://maven.apache.org/guides/mini/guide-proxies.html)
- [DNS Resolution in Proxied Container Environments](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/)
