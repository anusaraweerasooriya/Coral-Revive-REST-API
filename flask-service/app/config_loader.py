import requests


def load_remote_config(url):
    try:
        response = requests.get(url)
        response.raise_for_status()
        config = response.json()
        return config
    except requests.exceptions.RequestException as e:
        print(f"Failed to load remote config: {e}")
        return {}
