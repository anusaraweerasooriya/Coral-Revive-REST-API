import requests
import socket
import atexit
import json

EUREKA_SERVER = "http://naming-server:8671/eureka"  # Use Docker service name
APP_NAME = "flask-service"
INSTANCE_PORT = 5000

def register_with_eureka():
    instance_id = f"{socket.gethostname()}:{APP_NAME}:{INSTANCE_PORT}"
    eureka_url = f"{EUREKA_SERVER}/apps/{APP_NAME}"
    
    # Get the container's IP address (can use a more reliable method if needed)
    try:
        ip_address = socket.gethostbyname(socket.gethostname())
    except socket.error as e:
        print(f"Failed to get IP address: {e}")
        ip_address = "127.0.0.1"

    data = {
        "instance": {
            "hostName": socket.gethostname(),
            "app": APP_NAME.upper(),
            "ipAddr": ip_address,
            "vipAddress": APP_NAME,
            "secureVipAddress": APP_NAME,
            "status": "UP",
            "port": {"$": INSTANCE_PORT, "@enabled": "true"},
            "dataCenterInfo": {
                "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                "name": "MyOwn"
            },
            "healthCheckUrl": f"http://{ip_address}:{INSTANCE_PORT}/health",
            "statusPageUrl": f"http://{ip_address}:{INSTANCE_PORT}/info",
            "homePageUrl": f"http://{ip_address}:{INSTANCE_PORT}/"
        }
    }
    
    headers = {'Content-Type': 'application/json'}
    try:
        response = requests.post(eureka_url, json=data, headers=headers)
        response.raise_for_status()
        print(f"Registered with Eureka: {instance_id}")
    except requests.exceptions.RequestException as e:
        print(f"Failed to register with Eureka: {e}")

    # Deregister on exit
    atexit.register(deregister_from_eureka, instance_id=instance_id)

def deregister_from_eureka(instance_id):
    try:
        response = requests.delete(f"{EUREKA_SERVER}/apps/{APP_NAME}/{instance_id}")
        response.raise_for_status()
        print(f"Deregistered from Eureka: {instance_id}")
    except requests.exceptions.RequestException as e:
        print(f"Failed to deregister from Eureka: {e}")

# Call the registration function
register_with_eureka()
