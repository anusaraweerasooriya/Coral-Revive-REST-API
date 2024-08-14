import requests
import socket
import atexit

EUREKA_SERVER = "http://localhost:8671/eureka"
APP_NAME = "flask-service"
INSTANCE_PORT = 5000


def register_with_eureka():
    instance_id = f"{socket.gethostname()}:{APP_NAME}:{INSTANCE_PORT}"
    eureka_url = f"{EUREKA_SERVER}/apps/{APP_NAME}"
    data = {
        "instance": {
            "hostName": socket.gethostname(),
            "app": APP_NAME.upper(),
            "ipAddr": socket.gethostbyname(socket.gethostname()),
            "vipAddress": APP_NAME,
            "secureVipAddress": APP_NAME,
            "status": "UP",
            "port": {"$": INSTANCE_PORT, "@enabled": "true"},
            "dataCenterInfo": {
                "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                "name": "MyOwn"
            }
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
        response = requests.delete(
            f"{EUREKA_SERVER}/apps/{APP_NAME}/{instance_id}")
        response.raise_for_status()
        print(f"Deregistered from Eureka: {instance_id}")
    except requests.exceptions.RequestException as e:
        print(f"Failed to deregister from Eureka: {e}")
