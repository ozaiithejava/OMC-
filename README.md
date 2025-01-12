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

## 📥 Installation  
1. [Download the latest release of OMC.](https://github.com/ozaiithejava/OMC/releases)  
2. Place the `OMC.jar` file in your server directory.  
3. Configure `server.properties` and `spigot.yml` as needed.  
4. Start your server using the following command:  
   ```bash
   java -Xms1G -Xmx2G -jar OMC.jar nogui
