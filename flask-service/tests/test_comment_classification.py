import unittest
from flask import json
from app import create_app
from colorama import Fore, Style, init

init(autoreset=True)

class TestCommentClassification(unittest.TestCase):
    """Test suite for the Comment Classification API."""

    def setUp(self):
        """Set up the test client and the testing environment."""
        self.app = create_app('testing')
        self.client = self.app.test_client()
        self.app.testing = True
        print(Fore.CYAN + "\nSetting up the testing environment...")

    def test_classify_comment_factual(self):
        """Test classification of a factual comment."""
        print(Fore.YELLOW + "\nRunning: test_classify_comment_factual")
        payload = {
            "post": "Coral reefs are vital for marine biodiversity, providing shelter and food for over 25% of all marine species. Without coral reefs, many marine ecosystems would collapse.",
            "comment": "It's scientifically proven that coral reefs are essential for marine life, supporting thousands of species including fish, mollusks, and crustaceans."
        }

        response = self.client.post(
            '/api/comment-verification/classify',
            data=json.dumps(payload),
            content_type='application/json'
        )

        data = json.loads(response.data)

        self.assertEqual(response.status_code, 200, "Expected HTTP 200 OK.")
        self.assertEqual(data['classification'], 'True', "Expected factual classification.")
        print(Fore.GREEN + "Test Passed!")

    def test_classify_comment_non_factual(self):
        """Test classification of a non-factual comment."""
        print(Fore.YELLOW + "\nRunning: test_classify_comment_non_factual")
        payload = {
            "post": "Coral reefs play a significant role in the protection of coastal areas, reducing the impact of waves, storms, and floods.",
            "comment": "Coral reefs are entirely man-made structures built to attract tourists to tropical regions."
        }

        response = self.client.post(
            '/api/comment-verification/classify',
            data=json.dumps(payload),
            content_type='application/json'
        )

        data = json.loads(response.data)

        self.assertEqual(response.status_code, 200, "Expected HTTP 200 OK.")
        self.assertEqual(data['classification'], 'False', "Expected non-factual classification.")
        print(Fore.GREEN + "Test Passed!")

    def test_classify_comment_semantic(self):
        """Test classification of a semantic comment."""
        print(Fore.YELLOW + "\nRunning: test_classify_comment_semantic")
        payload = {
            "post": "Coral reefs are declining at an alarming rate due to climate change, pollution, and overfishing. This has led to a significant decrease in marine biodiversity.",
            "comment": "This is such a sad situation! I really hope we can find solutions to save the reefs."
        }

        response = self.client.post(
            '/api/comment-verification/classify',
            data=json.dumps(payload),
            content_type='application/json'
        )

        data = json.loads(response.data)

        self.assertEqual(response.status_code, 200, "Expected HTTP 200 OK.")
        self.assertEqual(data['classification'], 'semantic', "Expected semantic classification.")
        print(Fore.GREEN + "Test Passed!")

    def test_classify_missing_fields(self):
        """Test response when comment field is missing."""
        print(Fore.YELLOW + "\nRunning: test_classify_missing_fields")
        payload = {
            "post": "Coral reefs are important for coastal protection."
        }

        response = self.client.post(
            '/api/comment-verification/classify',
            data=json.dumps(payload),
            content_type='application/json'
        )

        self.assertEqual(response.status_code, 400, "Expected HTTP 400 Bad Request.")
        data = json.loads(response.data)
        self.assertEqual(data['error'], "Both 'post' and 'comment' fields are required", 
                         "Expected missing fields error message.")
        print(Fore.GREEN + "Test Passed!")

    def tearDown(self):
        """Clean up after each test case."""
        print(Fore.CYAN + "Cleaning up after the test...")

if __name__ == '__main__':
    print(Fore.MAGENTA + "Starting Comment Classification Test Suite...\n")
    unittest.main(verbosity=2)
