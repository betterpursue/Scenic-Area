import requests
import json
import random
import string

BASE_URL = "http://localhost:8080/api/v1/redeem"
MERCHANT_ID = "1"  # Assuming a merchant with ID 1 exists for testing

def generate_random_code(length=10):
    """Generates a random alphanumeric code."""
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=length))

def test_redeem_code():
    """Tests the /redeem endpoint."""
    print("--- Testing POST /redeem ---")
    headers = {"Content-Type": "application/json; charset=utf-8", "X-Merchant-Id": MERCHANT_ID}
    
    code_to_redeem = "TESTCODE123"
    print(f"Attempting to redeem code: {code_to_redeem}")
    data = {"code": code_to_redeem}
    
    try:
        response = requests.post(BASE_URL, headers=headers, data=json.dumps(data))
        response.raise_for_status()  # Raise an exception for bad status codes
        
        print(f"Headers: {response.headers}")
        response_data = json.loads(response.content.decode('utf-8'))
        print(f"Response: {response_data}")
        assert response_data["code"] == 200
        assert response_data["message"] == "核销成功"
        print("Successfully redeemed a code.")

    except requests.exceptions.RequestException as e:
        print(f"An error occurred: {e}")
        if e.response:
            print(f"Response body: {e.response.text}")


def test_get_redeem_records():
    """Tests the /records endpoint."""
    print("\n--- Testing GET /redeem/records ---")
    headers = {"X-Merchant-Id": MERCHANT_ID}
    
    try:
        response = requests.get(f"{BASE_URL}/records", headers=headers)
        response.raise_for_status()
        
        print(f"Headers: {response.headers}")
        response_data = json.loads(response.content.decode('utf-8'))
        print(f"Response: {response_data}")
        assert response_data["code"] == 200
        assert "data" in response_data
        print("Successfully fetched redeem records.")

    except requests.exceptions.RequestException as e:
        print(f"An error occurred: {e}")
        if e.response:
            print(f"Response body: {e.response.text}")


def test_get_statistics():
    """Tests the /statistics endpoint."""
    print("\n--- Testing GET /redeem/statistics ---")
    headers = {"X-Merchant-Id": MERCHANT_ID}
    params = {"period": "WEEK"}
    
    try:
        response = requests.get(f"{BASE_URL}/statistics", headers=headers, params=params)
        response.raise_for_status()
        
        print(f"Headers: {response.headers}")
        response_data = json.loads(response.content.decode('utf-8'))
        print(f"Response: {response_data}")
        assert response_data["code"] == 200
        assert "data" in response_data
        print("Successfully fetched statistics.")

    except requests.exceptions.RequestException as e:
        print(f"An error occurred: {e}")
        if e.response:
            print(f"Response body: {e.response.text}")

if __name__ == "__main__":
    test_redeem_code()
    test_get_redeem_records()
    test_get_statistics()
