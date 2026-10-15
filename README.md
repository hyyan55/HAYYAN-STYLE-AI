# HAYYAN STYLE AI — Menswear Personal Stylist

> **Tagline**: Your Wardrobe. Your Style. | **خزانتك، أناقتك، اختيارك.**  
> **Developer**: Hayyan Mohamed (حيان محمد)  
> **WhatsApp Contact**: [+249927309195](https://wa.me/249927309195)  
> **App Version**: v1.0.0  

---

## 1. Overview & Mission

**HAYYAN STYLE AI** is an AI-powered personal stylist application for men. It helps users organize their actual clothing collection, evaluate color harmony with fashion rules, and discover stylish outfit combinations from clothes they already own—without requiring expensive wardrobe purchases.

---

## 2. Key Features

- **Digital Wardrobe**:
  - Add clothing with device photo picker (`ActivityResultContracts.PickVisualMedia`).
  - Stores images locally in secure internal app storage with compression.
  - Categories: Tops (قمصان وتيشيرتات), Bottoms (بناطيل), Footwear (أحذية), Outerwear (سترات ومعاطف), Accessories (إكسسوارات).
  - Search, filter by category and style, mark items as unavailable/in laundry.
  - Pre-populated with a starter capsule wardrobe on first run.

- **AI Clothing Recognition**:
  - Multimodal vision analysis powered by Google Gemini API.
  - Automatically identifies category, garment type, primary & secondary colors, pattern, style, and suggested title.
  - Smart local fallback using pixel sampling if offline or without API key.

- **Deterministic Outfit Styling Engine**:
  - Occasion-based styling (University, Casual, Smart Casual, Formal, Family, Evening, Traditional).
  - Evaluates color harmony (contrast rules, monochromatic tones, complementary shades).
  - Strict wardrobe integrity: uses only available clothes from your wardrobe.
  - Top 3 diverse outfit recommendations with styling scores (0–100) and rationale.

- **Favorites & Personal Feedback**:
  - Save outfits to favorites, rate with like/dislike to calibrate recommendations.
  - One-tap sharing of outfit advice.

- **Arabic & English Full Localization**:
  - Default language is Arabic with full RTL layout.
  - Seamless toggle to English with LTR layout.

- **Data Privacy & Zero Forced Cost**:
  - Runs 100% locally on device using Room SQLite.
  - Export and import wardrobe metadata in standard JSON.
  - No account or credit card required.

---

## 3. Technology Stack

- **Platform**: Android Native (Jetpack Compose, Kotlin 2.2)
- **UI Architecture**: Material Design 3 (M3) with tailored luxury menswear color palette
- **Local Persistence**: Room SQLite (`androidx.room`) + internal file storage
- **Image Handling**: Coil Compose (`io.coil-kt:coil-compose`)
- **Typography**: Bundled "Cairo" typeface via `font-util`
- **AI Multimodal**: Gemini API (`gemini-2.5-flash` / `gemini-3.8-flash`) via `BuildConfig.GEMINI_API_KEY` with graceful local vision fallback

---

## 4. Setup & Running Instructions

### Android App Execution
The application builds and runs in Google AI Studio:
```bash
# Run unit tests
gradle :app:testDebugUnitTest

# Assemble debug APK
gradle :app:assembleDebug
```

### AI Configuration (Optional)
To enable cloud multimodal clothing recognition:
1. Open the **Secrets panel in AI Studio**.
2. Add your `GEMINI_API_KEY`.
3. If no key is provided, the application automatically uses the local vision extractor.

---

## 5. Developer Credits & Contact

- **Developer**: Hayyan Mohamed (المطور حيان محمد)
- **WhatsApp**: [+249927309195](https://wa.me/249927309195)
- **Prefilled WhatsApp message**: `السلام عليكم، لدي استفسار بخصوص تطبيق HAYYAN STYLE AI.`
