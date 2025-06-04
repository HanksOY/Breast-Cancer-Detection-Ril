1. Overview
Ril is an Android-based AI assistant application designed to assist doctors and clinicians in making medical decisions related to breast cancer detection, prediction, and treatment recommendation. The app leverages a Large Language Model (LLM, model TBD) for natural language understanding and generation, Ultravox for real-time voice translation and output, and image/file processing capabilities for analyzing medical inputs. The app supports multiple input modalities (text, voice, image, file) and provides simultaneous text and voice responses to enhance usability in clinical settings.

1.1 Objectives
Enable doctors to interact with the AI assistant via text, voice, image, or file inputs.
Provide accurate, context-aware responses for breast cancer-related queries, including detection, prediction, and treatment recommendations.
Deliver simultaneous text and voice outputs for all interactions to support accessibility and efficiency.
Ensure real-time voice interaction with low latency using Ultravox.
Maintain a modular architecture to allow flexibility in selecting the LLM model later.
Ensure compliance with medical data privacy standards (e.g., HIPAA, if applicable).
Create an intuitive and professional UI/UX tailored for clinicians.
1.2 Target Audience
Doctors, clinicians, and medical professionals involved in breast cancer diagnosis and treatment.
Secondary users: Medical researchers or trainees interacting with the app for educational purposes.
1.3 Platform
Android (developed using Android Studio).
Minimum SDK: API 24 (Android 7.0 Nougat) to ensure compatibility with modern devices.
Target SDK: Latest stable API (API 35 as of April 2025, if available).
2. Functional Requirements
2.1 User Input Modalities
The app must support four input types, each triggering simultaneous text and voice responses.

2.1.1 Text Input
Description: Users can type a query or command related to breast cancer (e.g., “Analyze this patient’s mammogram results” or “Recommend treatment for stage 2 breast cancer”).
Requirements:
Provide a text input field in the main UI.
Support multi-line input for detailed queries.
Include a “Submit” button or auto-submit on pressing Enter.
Validate input to prevent empty submissions (display error: “Please enter a query”).
Sanitize input to prevent injection attacks or malformed data.
Pass the text to the LLM for processing.
Store recent queries locally (e.g., SQLite database) for session history, with a limit of 50 entries.
Output:
Display the LLM’s response as text in a scrollable chat-like UI.
Convert the response to voice using Ultravox’s text-to-speech (TTS) and play it simultaneously.
Allow users to pause/resume voice output.
2.1.2 Voice Input (Real-Time)
Description: Users can speak to the assistant in real-time, and the app responds with voice and text outputs.
Requirements:
Implement real-time speech-to-text (STT) using Android’s SpeechRecognizer API or Ultravox’s STT capabilities.
Display a microphone button to initiate voice input.
Show a visual indicator (e.g., waveform) during recording.
Support continuous listening with a configurable timeout (e.g., 10 seconds of silence to end input).
Convert spoken input to text and display it in the UI for user confirmation.
Pass the transcribed text to the LLM for processing.
Handle noisy environments with noise cancellation (if supported by Ultravox or Android APIs).
Provide a “Stop” button to manually end voice input.
Ensure low latency (<500ms) for transcription and response initiation.
Output:
Display the LLM’s response as text in the chat UI.
Use Ultravox’s TTS to generate and play the response as voice simultaneously.
Allow users to interrupt voice output (e.g., by tapping a “Pause” button).
Cache recent voice interactions locally for session history.
2.1.3 Image Input
Description: Users can upload images (e.g., mammograms, pathology slides) for analysis related to breast cancer detection or diagnosis.
Requirements:
Provide an “Upload Image” button in the UI.
Support image sources: device gallery, camera, or file picker.
Accept common medical image formats: JPEG, PNG, DICOM (if feasible within scope).
Limit image size to 10MB to optimize processing.
Display a preview of the uploaded image in the UI.
Allow users to add a text description with the image (e.g., “Analyze this mammogram for abnormalities”).
Pass the image and optional text to the LLM or a specialized vision model (e.g., fine-tuned for medical imaging, TBD).
Implement basic image preprocessing (e.g., resizing, normalization) if required by the model.
Ensure secure handling of sensitive medical images (e.g., local storage with encryption).
Output:
Display the LLM’s analysis as text (e.g., “The mammogram shows a potential mass in the upper left quadrant”).
Use Ultravox’s TTS to read the response aloud simultaneously.
Highlight key findings in the text output (e.g., bold or color for “abnormalities detected”).
Allow users to save the analysis for later reference.
2.1.4 File Input
Description: Users can upload files (e.g., PDF reports, CSV datasets, or text-based medical records) for analysis related to breast cancer detection, prediction, or treatment recommendations.
Requirements:
Provide an “Upload File” button in the UI, distinct from the image upload option.
Support file sources: device storage or cloud storage (via Android’s Storage Access Framework).
Accept common file formats: PDF, CSV, TXT, and DOCX (if feasible).
Limit file size to 20MB to ensure efficient processing.
Display file metadata (e.g., name, size, type) in the UI after upload.
Allow users to add a text description with the file (e.g., “Analyze this pathology report for cancer staging”).
Extract text content from files:
For PDFs: Use a library like PdfBox or iText to extract text.
For CSVs: Parse structured data using a CSV parser (e.g., OpenCSV).
For TXT/DOCX: Read raw text or convert to plain text.
Pass the extracted text or structured data to the LLM for processing.
For CSVs, support basic data summarization (e.g., “This dataset contains 100 patient records with columns: age, tumor size, stage”).
Implement validation to reject unsupported file types (display error: “Unsupported file format, please upload PDF, CSV, TXT, or DOCX”).
Ensure secure handling of sensitive medical files (e.g., encrypt temporary storage, delete after processing unless saved).
Store file analysis history locally (e.g., SQLite) with a limit of 50 entries, including file name and response summary.
Output:
Display the LLM’s analysis as text in the chat UI (e.g., “The pathology report indicates stage 2B breast cancer with ER+ status”).
For CSVs, include summarized insights (e.g., “Average tumor size: 2.5 cm, 60% of patients are stage 2”).
Use Ultravox’s TTS to read the response aloud simultaneously.
Highlight key findings in the text output (e.g., bold or color for critical metrics like “stage 2B”).
Allow users to save the analysis (e.g., export as PDF) or revisit it in session history.
Provide an option to visualize CSV data (e.g., simple table or chart) if relevant to the query.
2.2 LLM Integration
Description: The app uses an LLM to process text, voice, image, and file inputs and generate contextually relevant responses.
Requirements:
Design a modular architecture to support multiple LLMs (e.g., LLaMA, Grok, or cloud-based APIs like OpenAI, to be finalized later).
Use a REST API or local inference (depending on the model) to send inputs and receive outputs.
Support multi-modal inputs (text + image + file) if the chosen LLM allows.
Fine-tune prompts for breast cancer expertise, e.g.:
“You are Ril, a medical AI assistant specialized in breast cancer. Provide accurate, concise, and professional responses for clinicians.”
Handle file inputs by summarizing content before passing to the LLM (e.g., extract key sections from PDFs or aggregate CSV data).
Handle errors gracefully (e.g., “Model unavailable, please try again”).
Implement retry logic for API failures (max 3 retries).
Cache frequent queries and responses locally to reduce API calls (respecting data privacy).
Log interactions for debugging (anonymized, no patient data).
2.3 Ultravox Integration
Description: Ultravox handles real-time voice translation (STT and TTS) and output for all interactions.
Requirements:
Integrate Ultravox SDK for Android (ensure compatibility with target SDK).
Configure Ultravox for low-latency STT and TTS.
Support English as the primary language, with potential for multilingual support (e.g., Spanish, French) in future iterations.
Allow customization of voice output (e.g., male/female voice, speed, pitch) via settings.
Ensure secure WebRTC connections for voice data.
Handle network interruptions (e.g., switch to offline mode or queue requests).
Optimize for battery efficiency during continuous voice interactions.
Log Ultravox performance metrics (e.g., latency, errors) for debugging.
2.4 Breast Cancer Detection, Prediction, and Treatment Recommendation
Description: The app provides AI-driven insights for breast cancer diagnosis, risk prediction, and treatment planning.
Requirements:
Detection:
Analyze text inputs (e.g., patient symptoms, lab results), image inputs (e.g., mammograms), and file inputs (e.g., pathology reports, CSV datasets) for signs of breast cancer.
Highlight abnormalities or risk factors (e.g., “Calcifications detected, recommend biopsy”).
Use evidence-based guidelines (e.g., BI-RADS for imaging) in responses.
Prediction:
Estimate risk or prognosis based on inputs (e.g., “Based on age 45, BRCA1 mutation, 5-year risk is X%”).
Integrate statistical models or LLM reasoning for predictions (TBD based on model capabilities).
Allow users to input structured data (e.g., age, family history, genetic markers) via forms or extracted from files (e.g., CSVs).
Treatment Recommendation:
Suggest treatment options based on stage, patient profile, and guidelines (e.g., NCCN, ASCO).
Example response: “For stage 1A ER+ breast cancer, consider lumpectomy + radiation.”
Clearly state that recommendations are AI-generated and require clinician review.
General:
Ensure responses are concise, professional, and tailored for clinicians (avoid patient-facing language).
Provide references to guidelines or studies when possible (e.g., “Per NCCN 2024 guidelines…”).
Allow users to request clarification or deeper analysis (e.g., “Explain the rationale”).
Flag uncertain or ambiguous inputs (e.g., “Insufficient data for accurate prediction, please provide tumor size”).
2.5 Assistant Role
Description: Ril acts as a supportive tool for doctors, not a decision-maker.
Requirements:
Include a disclaimer in the UI and responses: “Ril provides AI-generated insights for informational purposes. Final decisions must be made by a licensed clinician.”
Design responses to augment clinical workflows (e.g., summarize findings, suggest next steps).
Allow users to export session data (e.g., PDF report of chat history, including file analyses) for patient records.
Support collaborative use cases (e.g., share analysis with another clinician via secure link, if feasible).
3. Non-Functional Requirements
3.1 Performance
Response time: <2 seconds for text input, <3 seconds for voice/image/file input (excluding network latency).
Voice interaction latency: <500ms for STT and TTS (Ultravox).
File processing time: <5 seconds for files up to 20MB (e.g., PDF text extraction, CSV parsing).
App startup time: <3 seconds.
Support concurrent usage for up to 5 sessions (e.g., multiple chats open).
3.2 Security and Privacy
Encrypt all user inputs and outputs locally (e.g., AES-256), including files.
Ensure no patient-identifiable data is stored permanently unless explicitly saved by the user.
Comply with medical data regulations (e.g., HIPAA, GDPR, depending on deployment region).
Use secure APIs (HTTPS, OAuth) for LLM and Ultravox interactions.
Implement user authentication (e.g., email/password or SSO) to restrict access to clinicians.
Securely delete temporary files after processing unless saved by the user.
3.3 Usability
Design a clean, professional UI with a medical aesthetic (e.g., white/blue color scheme).
Ensure accessibility: support screen readers, high-contrast mode, and scalable fonts.
Provide tooltips or help icons for complex features (e.g., file upload, CSV analysis).
Support offline mode for cached responses (limited functionality).
3.4 Scalability
Design the backend (if cloud-based) to handle 1,000 concurrent users.
Optimize LLM API calls to minimize costs (e.g., batch requests, cache common queries).
Allow easy updates to the LLM model or Ultravox version without major code changes.
3.5 Reliability
Achieve 99.9% uptime for cloud-based components.
Implement crash reporting (e.g., Firebase Crashlytics) to monitor app stability.
Handle edge cases (e.g., malformed files, network drops) with user-friendly errors.
4. UI/UX Design
Refer to uploaded image and prompt.
5. Technical Requirements
5.1 Architecture
Frontend: Android (Kotlin/Java) using Jetpack Compose for UI.
Backend: REST API for LLM and Ultravox integration (serverless, e.g., AWS Lambda, if cloud-based).
Database: SQLite for local storage of chat history, cached responses, and file metadata.
Networking: Retrofit or Ktor for API calls, OkHttp for WebSocket (Ultravox).
Dependency Injection: Hilt or Koin for managing dependencies.
State Management: ViewModel with LiveData/Flow for reactive UI updates.
5.2 Libraries and APIs
Ultravox: For real-time STT and TTS.
Android SpeechRecognizer: Fallback for offline STT.
Glide/Picasso: For image loading and caching.
Room: For SQLite database.
WorkManager: For background tasks (e.g., caching, file uploads).
Firebase: For analytics and crash reporting (optional).
LLM API: Placeholder for chosen model (e.g., Hugging Face, OpenAI, or local inference with ONNX).
File Processing:
PdfBox/iText: For PDF text extraction.
OpenCSV: For CSV parsing.
Apache POI: For DOCX text extraction (if supported).
5.3 Folder Structure (modifiable)
text

/app
  /data
    /local      # Room database, cached responses
    /remote     # API clients for LLM, Ultravox
    /model      # Data classes, entities
  /di           # Dependency injection modules
  /ui
    /main       # Main screen, chat UI
    /settings   # Settings screen
    /components # Reusable UI components
  /utils
    /file       # File processing (PDF, CSV, TXT, DOCX)
    /image      # Image processing
    /logging    # Logging helpers
  /viewmodel    # ViewModels for state management
5.4 Testing
Unit Tests: ViewModels, API clients, file parsers (JUnit, Mockito).
UI Tests: Espresso for main workflows (text input, voice input, image upload, file upload).
Integration Tests: LLM, Ultravox, and file processing interactions.
Code Coverage: Target 80% for critical modules.
6. Development Guidelines for Cursor
6.1 Code Generation Instructions
Language: Use Kotlin for Android development.
UI Framework: Use Jetpack Compose for all UI components.
Modularity: Generate code in small, reusable modules (e.g., separate files for text input, voice input, image processing, file processing).
Comments: Include detailed comments explaining each class/function’s purpose.
Error Handling: Add try-catch blocks for API calls, file operations, and network tasks.
Naming: Follow Android conventions (e.g., MainActivity, ChatViewModel, FileInputScreen).
Sample Code:
Generate a MainActivity with a Compose-based chat UI.
Create a ChatViewModel to handle text, voice, image, and file inputs.
Implement a UltravoxService class for STT/TTS integration.
Provide a FileProcessor utility for handling PDFs, CSVs, TXT, and DOCX files.
Provide an ImageProcessor utility for handling medical images.
Include a BreastCancerAnalyzer class with placeholder logic for detection/prediction.
6.2 Suggested Workflow
Scaffold the App:
Generate MainActivity, MainScreen (Compose), and basic navigation.
Set up Hilt for dependency injection.
Configure Room for local storage.
Implement Input Handlers:
Text input: Create TextInputScreen with validation.
Voice input: Create VoiceInputScreen with Ultravox integration.
Image input: Create ImageInputScreen with gallery/camera support.
File input: Create FileInputScreen with storage/cloud support and file parsing.
Integrate LLM:
Create LLMService with a mock API client (replaceable later).
Implement prompt engineering for breast cancer queries, including file data.
Add Ultravox:
Configure UltravoxService for real-time STT/TTS.
Test voice input/output in a sandbox environment.
Build Breast Cancer Logic:
Generate BreastCancerAnalyzer with rule-based or API-driven logic.
Add detection, prediction, and recommendation functions for text, image, and file inputs.
Polish UI/UX:
Style the app with Material Design 3.
Add animations for smooth transitions.
Test and Debug:
Write unit tests for ViewModels, services, and file parsers.
Run UI tests for main workflows.
6.3 Assumptions for Code Generation
LLM model is accessed via a REST API (e.g., POST /generate endpoint).
Ultravox provides STT/TTS via WebRTC with Android SDK support.
Image analysis uses a pre-trained model or LLM with vision capabilities (TBD).
File processing assumes text-based extraction; complex formats (e.g., DICOM) may require additional libraries.
No external database is required beyond local SQLite.
App runs on devices with 4GB+ RAM and modern CPUs for image/voice/file processing.
