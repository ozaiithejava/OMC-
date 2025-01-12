package ozaii.draw;

import java.util.HashMap;
import java.util.Map;

public class DrawManager {

    private Map<Integer, Draw> drawMap;
    private int nextId;

    public DrawManager() {
        this.drawMap = new HashMap<>();
        this.nextId = 1; // İlk id 1'den başlayacak
    }

    public Draw createDraw(String prize, int winnerCount, long duration) {
        Draw draw = new Draw(nextId++, prize, winnerCount, duration);
        draw.start(); // Çekilişi başlatıyoruz ve bitiş zamanını hesaplıyoruz.
        drawMap.put(draw.getId(), draw);
        return draw;
    }

    public Draw getDraw(int id) {
        return drawMap.get(id);
    }

    public void removeDraw(int id) {
        drawMap.remove(id);
    }

    public Map<Integer, Draw> getDrawMap() {
        return drawMap;
    }

    // Aktif olan bir çekilişi döndürür (bitiş zamanı geçmemiş olan)
    public Draw getActiveDraw() {
        for (Draw draw : drawMap.values()) {
            if (!draw.isExpired()) {
                return draw;  // İlk aktif çekilişi döndürür
            }
        }
        return null; // Eğer aktif bir çekiliş yoksa, null döndürür
    }
}
