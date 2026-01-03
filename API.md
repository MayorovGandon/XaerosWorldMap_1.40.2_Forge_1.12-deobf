# API для разработчиков - Xaero's World Map

## Введение

Этот документ описывает API для расширения функциональности Xaero's World Map. API позволяет добавлять кастомные элементы на карту, создавать подсветки, интегрироваться с другими модами и кастомизировать рендеринг.

## Получение доступа к API

### Получение текущей сессии

```java
WorldMapSession session = WorldMapSession.getCurrentSession();
if (session != null && session.isUsable()) {
    MapProcessor processor = session.getMapProcessor();
    // Использование процессора
}
```

### Получение сессии для игрока

```java
EntityPlayerSP player = Minecraft.getMinecraft().player;
WorldMapSession session = WorldMapSession.getForPlayer(player);
```

## Добавление элементов на карту

### Интерфейс MapElement

Базовый интерфейс для элементов карты:

```java
public interface MapElement {
    // Методы для получения позиции, размера и т.д.
}
```

### Реализация кастомного элемента

```java
public class CustomMapElement implements MapElement {
    private double x, y, z;
    private String name;
    
    // Реализация методов интерфейса
    @Override
    public double getX() {
        return x;
    }
    
    @Override
    public double getY() {
        return y;
    }
    
    @Override
    public double getZ() {
        return z;
    }
    
    // Дополнительные методы
}
```

### Регистрация элементов

Элементы регистрируются через `MapElementRenderHandler`:

```java
WorldMapSession session = WorldMapSession.getCurrentSession();
if (session != null) {
    MapElementRenderHandler handler = WorldMap.mapElementRenderHandler;
    // Регистрация элемента
}
```

### Рендеринг элементов

Для кастомного рендеринга реализуйте `MapElementRenderer`:

```java
public class CustomElementRenderer implements MapElementRenderer {
    @Override
    public void render(MapElement element, MapElementRenderLocation location) {
        // Кастомный рендеринг
    }
}
```

## Создание подсветок

### AbstractHighlighter

Базовый класс для создания подсветок:

```java
public class CustomHighlighter extends AbstractHighlighter {
    
    public CustomHighlighter() {
        super("custom_highlighter_id");
    }
    
    @Override
    public void prepareHighlights(MapRegion region) {
        // Подготовка подсветок для региона
    }
    
    @Override
    public void renderHighlights(MapRegion region, float partialTicks) {
        // Рендеринг подсветок
    }
}
```

### Регистрация подсветки

```java
WorldMapSession session = WorldMapSession.getCurrentSession();
if (session != null) {
    MapProcessor processor = session.getMapProcessor();
    HighlighterRegistry registry = processor.getHighlighterRegistry();
    
    CustomHighlighter highlighter = new CustomHighlighter();
    registry.register(highlighter);
}
```

### ChunkHighlighter

Для подсветки чанков:

```java
public class CustomChunkHighlighter extends ChunkHighlighter {
    
    @Override
    public boolean shouldHighlight(int chunkX, int chunkZ) {
        // Логика определения, нужно ли подсвечивать чанк
        return true;
    }
    
    @Override
    public int getColor(int chunkX, int chunkZ) {
        // Возврат цвета подсветки
        return 0xFF0000; // Красный
    }
}
```

## Система трекинга игроков

### IPlayerTrackerSystem

Интерфейс для создания кастомных систем трекинга:

```java
public class CustomPlayerTrackerSystem implements IPlayerTrackerSystem {
    
    @Override
    public void onPlayerJoin(EntityPlayer player) {
        // Обработка входа игрока
    }
    
    @Override
    public void onPlayerLeave(EntityPlayer player) {
        // Обработка выхода игрока
    }
    
    @Override
    public void update() {
        // Обновление системы
    }
    
    @Override
    public Collection<EntityPlayer> getTrackedPlayers() {
        // Возврат отслеживаемых игроков
        return Collections.emptyList();
    }
}
```

### Регистрация системы трекинга

```java
PlayerTrackerSystemManager manager = WorldMap.playerTrackerSystemManager;
manager.register("custom_tracker", new CustomPlayerTrackerSystem());
```

### SyncedPlayerTrackerSystem

Для синхронизированных систем:

```java
public class CustomSyncedTracker extends SyncedPlayerTrackerSystem {
    
    @Override
    protected void handleSyncPacket(XaeroPacket packet) {
        // Обработка пакетов синхронизации
    }
}
```

## Интеграция с модами

### SupportMods

Для интеграции с другими модами:

```java
// Проверка наличия мода
if (SupportMods.isModLoaded("modid")) {
    // Интеграция
}

// Регистрация кастомной интеграции
SupportMods.registerCustomIntegration("modid", new CustomIntegration());
```

### Пример интеграции

```java
public class CustomModIntegration {
    
    public void init() {
        // Инициализация интеграции
    }
    
    public void onWorldLoad(World world) {
        // Обработка загрузки мира
    }
}
```

## Работа с регионами

### Получение региона

```java
WorldMapSession session = WorldMapSession.getCurrentSession();
if (session != null) {
    MapProcessor processor = session.getMapProcessor();
    MapWorld mapWorld = processor.getMapWorld();
    MapDimension dimension = mapWorld.getCurrentDimension();
    
    if (dimension != null) {
        MapRegion region = dimension.getRegion(regionX, regionZ);
        if (region != null) {
            // Работа с регионом
        }
    }
}
```

### Чтение данных региона

```java
MapRegion region = ...;
if (region != null && region.isLoaded()) {
    MapTileChunk chunk = region.getChunk(chunkX, chunkZ);
    if (chunk != null) {
        // Чтение данных чанка
    }
}
```

## Работа с настройками

### Доступ к настройкам

```java
WorldMap mod = WorldMap.INSTANCE;
ConfigChannel configs = mod.getConfigs();

// Получение клиентских настроек
IClientConfigManager clientConfig = configs.getClientConfigManager();
IClientConfigProfile currentProfile = clientConfig.getCurrentProfile();

// Получение основных настроек
IPrimaryClientConfigManager primaryConfig = configs.getPrimaryClientConfigManager();
```

### Чтение настроек

```java
// Пример чтения настройки
ConfigOption option = WorldMapPrimaryClientConfigOptions.SOME_OPTION;
Object value = primaryConfig.getConfig().get(option);
```

### Запись настроек

```java
// Пример записи настройки
primaryConfig.getConfig().set(option, newValue);
primaryConfig.getIO().save();
```

## Работа с биомами

### Получение информации о биоме

```java
MapBiomes mapBiomes = WorldMap.mapBiomes;
BiomeInfoSupplier supplier = mapBiomes.getBiomeInfoSupplier();

// Получение информации о биоме в позиции
BiomeInfo info = supplier.getBiomeInfo(world, pos);
if (info != null) {
    int color = info.getColor();
    // Использование цвета
}
```

### Расчет цветов биомов

```java
BiomeColorCalculator calculator = ...;
int color = calculator.calculateBiomeColor(world, pos, biome);
```

## Работа с текстурами

### Загрузка текстур

```java
TextureUploader uploader = ...;
TextureUpload upload = uploader.createUpload(textureData, width, height);

// Загрузка в GPU
uploader.upload(upload);
```

### Работа с пулами текстур

```java
// Получение из пула
TextureUploadPool.Normal pool = WorldMap.normalTextureUploadPool;
TextureUpload upload = pool.get();

// Возврат в пул после использования
pool.put(upload);
```

## События

### Подписка на события

```java
@SubscribeEvent
public void onMapUpdate(MapUpdateEvent event) {
    // Обработка обновления карты
}

// Регистрация
MinecraftForge.EVENT_BUS.register(this);
```

### Кастомные события

```java
public class CustomMapEvent extends Event {
    // Данные события
}

// Отправка события
MinecraftForge.EVENT_BUS.post(new CustomMapEvent());
```

## Экспорт карты

### Использование PNGExporter

```java
PNGExporter exporter = WorldMap.pngExporter;

// Экспорт региона
exporter.exportRegion(region, outputPath);

// Экспорт всего измерения
exporter.exportDimension(dimension, outputPath);
```

## Работа с измерениями

### Получение измерения

```java
MapWorld mapWorld = processor.getMapWorld();
MapDimension dimension = mapWorld.getDimension(dimensionId);
```

### Переключение измерений

```java
MapDimension currentDim = mapWorld.getCurrentDimension();
if (currentDim != null) {
    // Работа с текущим измерением
}
```

## Утилиты

### Работа с координатами

```java
// Конвертация координат
int regionX = MapRegion.getRegionCoord(blockX);
int regionZ = MapRegion.getRegionCoord(blockZ);

int chunkX = blockX >> 4;
int chunkZ = blockZ >> 4;
```

### Работа с пулами

```java
// Получение из пула плиток
MapTilePool tilePool = WorldMap.tilePool;
MapTile tile = tilePool.get();

// Возврат в пул
tilePool.put(tile);
```

## Обработка ошибок

### Безопасный доступ

```java
try {
    WorldMapSession session = WorldMapSession.getCurrentSession();
    if (session != null && session.isUsable()) {
        // Безопасная работа с сессией
    }
} catch (Exception e) {
    WorldMap.LOGGER.error("Ошибка при работе с API", e);
}
```

### Использование CrashHandler

```java
try {
    // Код, который может вызвать ошибку
} catch (Throwable t) {
    WorldMap.crashHandler.setCrashedBy(t);
    // Обработка ошибки
}
```

## Примеры использования

### Пример 1: Добавление кастомной иконки

```java
public class CustomIconElement implements MapElement {
    private final double x, y, z;
    private final ResourceLocation texture;
    
    public CustomIconElement(double x, double y, double z, ResourceLocation texture) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.texture = texture;
    }
    
    @Override
    public double getX() { return x; }
    
    @Override
    public double getY() { return y; }
    
    @Override
    public double getZ() { return z; }
    
    public ResourceLocation getTexture() {
        return texture;
    }
}
```

### Пример 2: Подсветка определенных чанков

```java
public class ImportantChunkHighlighter extends ChunkHighlighter {
    private final Set<ChunkPos> importantChunks = new HashSet<>();
    
    public void addImportantChunk(int chunkX, int chunkZ) {
        importantChunks.add(new ChunkPos(chunkX, chunkZ));
    }
    
    @Override
    public boolean shouldHighlight(int chunkX, int chunkZ) {
        return importantChunks.contains(new ChunkPos(chunkX, chunkZ));
    }
    
    @Override
    public int getColor(int chunkX, int chunkZ) {
        return 0x00FF00; // Зеленый
    }
}
```

### Пример 3: Кастомная система трекинга

```java
public class TeamTrackerSystem implements IPlayerTrackerSystem {
    private final Map<UUID, EntityPlayer> trackedPlayers = new HashMap<>();
    
    public void addTeamMember(UUID playerId, EntityPlayer player) {
        trackedPlayers.put(playerId, player);
    }
    
    @Override
    public Collection<EntityPlayer> getTrackedPlayers() {
        return trackedPlayers.values();
    }
    
    @Override
    public void update() {
        // Обновление позиций игроков
    }
}
```

## Лучшие практики

1. **Всегда проверяйте наличие сессии** перед использованием API
2. **Используйте try-catch** для обработки ошибок
3. **Освобождайте ресурсы** после использования (возвращайте в пулы)
4. **Не блокируйте главный поток** - используйте асинхронные операции
5. **Логируйте ошибки** через `WorldMap.LOGGER`
6. **Проверяйте версию мода** перед использованием новых функций
7. **Используйте пулы ресурсов** для оптимизации производительности

## Ограничения

- API доступен только на клиенте
- Некоторые функции требуют активной сессии карты
- Изменения в структуре данных могут потребовать обновления кода
- Производительность зависит от количества кастомных элементов

## Поддержка

Для вопросов и поддержки обращайтесь к официальным ресурсам мода или создавайте issues в репозитории проекта.

