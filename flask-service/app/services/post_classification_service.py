from transformers import GPT2LMHeadModel, GPT2Tokenizer
import torch

class PostClassificationService:
    def __init__(self):
        # Use the specified GPT-2 model directory
        gpt2_dir = "/app/models/comment-validation-service/gpt2_model"
        try:
            # Load GPT-2 model and tokenizer
            self.gpt2_model = GPT2LMHeadModel.from_pretrained(gpt2_dir).to(torch.device('cuda' if torch.cuda.is_available() else 'cpu'))
            self.gpt2_tokenizer = GPT2Tokenizer.from_pretrained(gpt2_dir)
            print(f"GPT-2 model loaded from: {gpt2_dir}")
        except Exception as e:
            print(f"Error loading GPT-2 model from {gpt2_dir}: {e}")
        
        # Define the candidate labels
        self.candidate_labels = [
            "Reef Restoration",
            "Coral Restoration",
            "Marine Conservation",
            "Coral Research",
            "Diving",
            "Corals",
            "Coral Bleaching",
            "Marine Biodiversity",
            "Coral Sri Lanka",
            "Coral Health",
            "Ocean Preservation",
            "Coastal Protection",
            "Marine Ecosystems"
        ]
    
    def gpt2_score_text(self, text):
        try:
            inputs = self.gpt2_tokenizer.encode(text, return_tensors='pt').to(torch.device('cuda' if torch.cuda.is_available() else 'cpu'))
            with torch.no_grad():
                outputs = self.gpt2_model(inputs, labels=inputs)
                loss = outputs.loss
            return loss.item()  # Lower loss indicates more likely text
        except Exception as e:
            print(f"Error during GPT-2 scoring: {e}")
            return float('inf')  # Return a high loss if there's an error

    def classify_text(self, text):
        try:
            # Prepare phrases to score for each candidate label
            scores = []
            for label in self.candidate_labels:
                prompt = f"The following text is related to {label.lower()}: {text}"
                score = self.gpt2_score_text(prompt)
                scores.append((label, score))

            # Sort the labels by their scores (lower score means higher relevance)
            scores = sorted(scores, key=lambda x: x[1])
            top_two_labels = [scores[0][0], scores[1][0]]
            top_two_scores = [scores[0][1], scores[1][1]]

            return {
                "text": text,
                "top_two_labels": top_two_labels,
                "top_two_scores": top_two_scores
            }
        except Exception as e:
            return {"error": str(e)}

# Example usage:
if __name__ == "__main__":
    service = PostClassificationService()
    result = service.classify_text("Coral reefs are vital to marine biodiversity and need to be preserved.")
    print("Classification result:", result)
