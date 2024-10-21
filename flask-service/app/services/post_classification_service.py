from transformers import pipeline

class PostClassificationService:
    def __init__(self):
        # Use the specified BART model directory
        bart_dir = "/app/models/comment-validation-service/bart-large-mnli"
        try:
            self.classifier = pipeline("zero-shot-classification", model=bart_dir)
            print(f"BART model loaded from: {bart_dir}")
        except Exception as e:
            print(f"Error loading BART model from {bart_dir}: {e}")
        
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
    
    def classify_text(self, text):
        try:
            result = self.classifier(text, self.candidate_labels)
            top_two_labels = result['labels'][:2]
            top_two_scores = result['scores'][:2]
            
            return {
                "text": text,
                "top_two_labels": top_two_labels,
                "top_two_scores": top_two_scores
            }
        except Exception as e:
            return {"error": str(e)}
