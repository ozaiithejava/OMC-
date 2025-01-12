
# OMC (Optimized Minecraft)  

OMC is an **asynchronous fork of Paper Spigot**, developed by **ozaii**, designed to provide maximum performance and stability for Minecraft servers running version **1.8.8**.  

## 🎯 Key Features  
- **Base Version:** 1.8.8  
- **Performance:**  
  - Highly optimized with **asynchronous task handling** for reduced latency and improved TPS.  
  - Optimized tick processing and removal of unnecessary overhead.  
- **Compatibility:**  
  - Fully compatible with the Paper API.  
  - Works seamlessly with existing Paper/Spigot plugins.  
- **Configuration:**  
  - Expanded customization options.  
  - New optimization settings for fine-tuned server management.  

## 🚀 Advantages  
- **Low Resource Usage:** Reduced memory and CPU consumption.  
- **Bug Fixes:** Resolves many issues present in Vanilla and Spigot.  
- **Stability:** Enhanced uptime and crash resistance.  

## 📦 Using the FactoryApi  
OMC includes a **FactoryApi** class that provides centralized access to various server managers and APIs. This makes it easy for developers to extend or integrate with OMC's features.  

### Available Components:  
- **Server Information:**  
  - `getServerName()`: Returns the server's name (`OMC`).  
  - `getServerVersion()`: Returns the server's current version.  
  - `getOwner()`: Returns the server owner's name (`ozaiithejava`).  
  - `getOwnerDiscord()`: Returns the server owner's Discord ID (`ozaii1337`).  

- **Factory Components:**  
  - `getDatabaseFactory()`: Provides access to the database factory.  
  - `getSettingsFactory()`: Provides access to the settings factory.  

- **Managers:**  
  - `getCoinManager()`: Access the CoinManager for managing in-game currency.  
  - `getLevelManager()`: Access the LevelManager for handling player levels.  
  - `getProfileManager()`: Access the ProfileManager for player profiles.  

- **APIs:**  
  - `getLevelManagerApi()`: Access the **LevelApi** for integrating player level-related operations.  
  - `getCoinManagerApi()`: Access the **CoinApi** for managing and querying in-game currency.  

### Example Usage  
To use the **FactoryApi**, simply initialize and call its static methods as needed:  
```java
import ozaii.apis.base.FactoryApi;

// Accessing server information
String serverName = FactoryApi.getServerName();
String serverVersion = FactoryApi.getServerVersion();
String owner = FactoryApi.getOwner();
String ownerDiscord = FactoryApi.getOwnerDiscord();

// Accessing managers and APIs
CoinManager coinManager = FactoryApi.getCoinManager();
LevelApi levelApi = FactoryApi.getLevelManagerApi();

// Using APIs
levelApi.increaseLevelAsync(playerUUID, 5);  // Add 5 levels to a player
coinManager.depositCoins(playerUUID, 100);  // Add 100 coins to a player
```

This centralized API simplifies development and ensures consistency across all integrations with OMC.  

## 🆕 New Features  
- **Performance Improvements:** Better overall server efficiency and reduced lag.  
- **TPS Optimization:** Ensures stable server tick rate under high load.  
- **Advanced Logging:** Enhanced log management for better debugging and monitoring.  
- **License Control:** Ensures proper usage and validation of the software.  
- **New Commands:** Added new administrative and player-focused commands.  
- **Systems and Managers:**  
  - **Coin/Level/Profile Manager** for advanced player tracking.  
  - **DatabaseFactory:** Simplifies database interactions.  
  - **LanguageFactory:** Easily manage translations and custom messages.  
  - **SettingsFactory:** Centralized configuration management.  
  - **FactoryAPI** and **ManagersAPI** for custom integrations.  

## 👥 Support and Contact  
- **Developer:** [ozaii](https://github.com/ozaiithejava)  
- **Discord:** ozaii1337  

We welcome community feedback! Feel free to report issues or suggest improvements on the [GitHub issues page.](https://github.com/ozaiithejava/OMC/issues)  

## 📄 License  
OMC is an open-source project released under the [MIT License](https://opensource.org/licenses/MIT).  

## 🌟 Contribute  
Contributions are always welcome! Fork the repository, create a feature branch, and open a pull request to submit your changes.  
