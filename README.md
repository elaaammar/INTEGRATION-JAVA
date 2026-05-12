# 🛡️ MindAudit — Système d'Audit Interne Intelligent basé sur l'IA

## PIDEV 3A — 2025/2026 | Intégration JavaFX ↔ Symfony

---

## 📋 Description du Projet

**MindAudit** est un système d'audit interne intelligent qui combine une application **Desktop JavaFX** et une application **Web Symfony** communiquant via des **API REST** en temps réel. Le projet permet de gérer l'ensemble du cycle d'audit (planification, exécution, reporting, recommandations) avec une synchronisation CRUD bidirectionnelle entre les deux plateformes.

---

## 🏗️ Architecture d'Intégration

```
┌──────────────────────────┐          HTTP/JSON          ┌──────────────────────────┐
│                          │  ◄───── API REST ──────►    │                          │
│    APPLICATION JAVAFX    │                             │   APPLICATION SYMFONY    │
│     (Desktop Client)     │   GET /api/audits           │     (Web Server)         │
│                          │   POST /api/reclamations    │                          │
│  ┌────────────────────┐  │   PUT /api/entreprises/1    │  ┌────────────────────┐  │
│  │ IntegrationManager │──┼──►DELETE /api/rapports/5    │  │  API Controllers   │  │
│  │                    │  │                             │  │                    │  │
│  │  SymfonyApiClient  │  │   ◄── JSON Response ──►    │  │  Entity Manager    │  │
│  │  AuthSyncService   │  │   {                         │  │  Doctrine ORM      │  │
│  │  AuditSyncService  │  │     "id": 1,                │  │  Security (JWT)    │  │
│  │  ReclamationSync   │  │     "name": "Audit 2025",   │  │                    │  │
│  │  EntrepriseSync    │  │     "status": "completed"   │  │                    │  │
│  │  RapportSync       │  │   }                         │  │                    │  │
│  │  RapportAuditSync  │  │                             │  │                    │  │
│  └────────────────────┘  │                             │  └────────────────────┘  │
│           │               │                             │           │              │
│  ┌────────▼───────────┐  │                             │  ┌────────▼───────────┐  │
│  │  Base MySQL locale │  │       Même base de données  │  │  Base MySQL locale │  │
│  │  mindaudit_java    │  │◄─────────────────────────►  │  │  mindaudit_web     │  │
│  └────────────────────┘  │                             │  └────────────────────┘  │
└──────────────────────────┘                             └──────────────────────────┘
        Port: 9000                                              Port: 8000
```

---

## 📦 Modules Intégrés

| # | Module | JavaFX (Package) | Symfony (Controller) | Sync |
|---|--------|-------------------|---------------------|------|
| 1 | **Gestion Utilisateurs** | `com.example.mindjavafx.model.User` | `UserController` | ✅ CRUD |
| 2 | **Gestion Audits** | `com.example.mindjavafx.model.Audit` | `AuditController` | ✅ CRUD |
| 3 | **Gestion Réclamations** | `com.gestionaudit.models.Reclamation` | `ReclamationController` | ✅ CRUD |
| 4 | **Rapports & Questions** | `com.gestion_audit.models.Rapport` | `RapportController` | ✅ CRUD |
| 5 | **Entreprises & Documents** | `com.gestion.entity.Entreprise` | `EntrepriseController` | ✅ CRUD |
| 6 | **Rapports IA (Risques, Reco.)** | `com.audit.auditaifx.model.RapportAudit` | `RapportAuditController` | ✅ CRUD |

---

## 🗂️ Structure du Projet JavaFX — Couche Intégration

```
src/main/java/com/example/mindjavafx/integration/
├── IntegrationManager.java              # Point d'entrée — initialise la connexion Symfony
├── client/
│   ├── ApiConfig.java                   # Configuration des endpoints API
│   └── SymfonyApiClient.java            # Client HTTP natif Java 11+ (GET/POST/PUT/DELETE)
├── dto/
│   ├── SyncResponse.java                # Réponse API générique typée
│   ├── LoginDTO.java                    # DTO authentification
│   ├── AuditDTO.java                    # DTO Audit
│   ├── ReclamationDTO.java              # DTO Réclamation
│   ├── ReponseReclamationDTO.java       # DTO Réponse Réclamation
│   ├── RapportDTO.java                  # DTO Rapport (+ QuestionDTO imbriqué)
│   ├── EntrepriseDTO.java               # DTO Entreprise
│   ├── DocumentDTO.java                 # DTO Document
│   └── RapportAuditDTO.java             # DTO Rapport IA (+ Recommandation, Risque)
└── service/
    ├── AuthSyncService.java             # Authentification JWT Symfony
    ├── AuditSyncService.java            # CRUD Audits synchronisé
    ├── ReclamationSyncService.java       # CRUD Réclamations synchronisé
    ├── RapportSyncService.java          # CRUD Rapports synchronisé
    ├── EntrepriseSyncService.java       # CRUD Entreprises synchronisé
    └── RapportAuditSyncService.java     # CRUD Rapports IA synchronisé
```

---

## 🚀 Installation & Lancement

### Prérequis
- **Java 17+** (JDK)
- **Maven** 3.8+
- **PHP 8.1+** avec Symfony CLI
- **MySQL** 5.7+ ou MariaDB
- **Composer** (gestionnaire PHP)

### Étape 1 — Démarrer Symfony (Web)
```bash
cd <votre-projet-symfony>
composer install
php bin/console doctrine:database:create
php bin/console doctrine:migrations:migrate
symfony server:start       # Démarre sur http://127.0.0.1:8000
```

### Étape 2 — Démarrer JavaFX (Desktop)
```bash
cd mindauditjava1-audit-feature-ui
mvn clean javafx:run
```

### Étape 3 — Vérifier la connexion
Au démarrage de JavaFX, la console affichera :
```
╔══════════════════════════════════════════════════╗
║  MindAudit — Intégration JavaFX ↔ Symfony       ║
║  PIDEV 3A — 2025/2026                           ║
╚══════════════════════════════════════════════════╝

[Integration] URL Symfony: http://127.0.0.1:8000
[Integration] Test de connexion...
[Integration] ✅ Serveur Symfony accessible — synchronisation activée
```

---

## 🔧 Configuration

Le fichier `config.properties` contient toutes les configurations :

```properties
# Base de données JavaFX
database.url=jdbc:mysql://localhost:3306/mindaudit_java
database.user=root
database.password=

# Intégration Symfony
symfony.base.url=http://127.0.0.1:8000
```

---

## 📡 Endpoints API Symfony attendus

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/login` | Authentification (retourne JWT) |
| `GET` | `/api/audits` | Liste tous les audits |
| `POST` | `/api/audits` | Créer un audit |
| `PUT` | `/api/audits/{id}` | Modifier un audit |
| `DELETE` | `/api/audits/{id}` | Supprimer un audit |
| `GET` | `/api/reclamations` | Liste les réclamations |
| `POST` | `/api/reclamations` | Créer une réclamation |
| `PUT` | `/api/reclamations/{id}` | Modifier une réclamation |
| `DELETE` | `/api/reclamations/{id}` | Supprimer une réclamation |
| `GET` | `/api/entreprises` | Liste les entreprises |
| `POST` | `/api/entreprises` | Créer une entreprise |
| `PUT` | `/api/entreprises/{id}` | Modifier une entreprise |
| `DELETE` | `/api/entreprises/{id}` | Supprimer une entreprise |
| `GET` | `/api/rapports` | Liste les rapports |
| `POST` | `/api/rapports` | Créer un rapport |
| `GET` | `/api/rapport-audits` | Liste les rapports IA |
| `POST` | `/api/rapport-audits` | Créer un rapport IA |

---

## 💻 Exemple d'Utilisation dans un Controller JavaFX

```java
import com.example.mindjavafx.integration.service.*;
import com.example.mindjavafx.integration.dto.*;

// Récupérer tous les audits depuis Symfony
List<AuditDTO> audits = AuditSyncService.getAll();

// Créer un nouvel audit (synchronisé dans Symfony)
AuditDTO newAudit = new AuditDTO();
newAudit.setName("Audit Sécurité Q1 2026");
newAudit.setCategory("Sécurité");
newAudit.setStatus("en_cours");
AuditDTO created = AuditSyncService.create(newAudit);

// Modifier un audit
created.setStatus("terminé");
AuditSyncService.update(created.getId(), created);

// Supprimer un audit
AuditSyncService.delete(created.getId());

// Réclamations
ReclamationDTO reclamation = new ReclamationDTO();
reclamation.setTitre("Problème de conformité");
reclamation.setPriorite("haute");
ReclamationSyncService.create(reclamation);
```

---

## 🛠️ Technologies Utilisées

### JavaFX (Desktop)
| Technologie | Version | Usage |
|-------------|---------|-------|
| Java | 17 | Langage principal |
| JavaFX | 17.0.2 | Interface graphique |
| Gson | 2.10.1 | Parsing JSON |
| java.net.http | JDK 11+ | Client HTTP natif |
| MySQL Connector | 8.0.33 | Base de données |
| Apache PDFBox | 2.0.29 | Génération PDF |
| ZXing | 3.5.2 | QR Codes |
| Apache POI | 5.2.3 | Export Excel |

### Symfony (Web)
| Technologie | Usage |
|-------------|-------|
| PHP 8.1+ | Langage serveur |
| Symfony 6+ | Framework web |
| Doctrine ORM | Mapping base de données |
| LexikJWT | Authentification API |
| NelmioCors | Gestion CORS |

---

## 👥 Équipe PIDEV 3A

**Projet** : Système d'Audit Interne Intelligent basé sur l'IA  
**Année** : 2025/2026  
**Établissement** : ESPRIT — École Supérieure Privée d'Ingénierie et de Technologies

---

## 📄 Licence

Ce projet est développé dans le cadre académique du PIDEV 3A à ESPRIT.