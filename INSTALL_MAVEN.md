# Установка Maven для сборки XILLEN Security Plugin

## Что такое Maven?

Maven - это инструмент для автоматизации сборки Java проектов. Он управляет зависимостями, компилирует код и создает JAR файлы.

## Установка Maven на Windows

### Шаг 1: Установка Java
1. Скачайте Java 17 или выше с официального сайта: https://www.oracle.com/java/technologies/downloads/
2. Установите Java, следуя инструкциям установщика
3. Добавьте Java в PATH (обычно это делается автоматически)

### Шаг 2: Скачивание Maven
1. Перейдите на официальный сайт Maven: https://maven.apache.org/download.cgi
2. Скачайте файл `apache-maven-3.9.6-bin.zip` (или новее)
3. Распакуйте архив в папку (например, `C:\Program Files\Apache\maven`)

### Шаг 3: Настройка переменных окружения
1. Нажмите `Win + R`, введите `sysdm.cpl`, нажмите Enter
2. Перейдите на вкладку "Дополнительно"
3. Нажмите "Переменные среды"
4. В разделе "Системные переменные" найдите переменную `Path`
5. Нажмите "Изменить" и добавьте путь к папке `bin` Maven
   - Например: `C:\Program Files\Apache\maven\bin`
6. Нажмите OK во всех окнах

### Шаг 4: Проверка установки
1. Откройте новое окно командной строки
2. Введите команду: `mvn -version`
3. Должна появиться информация о версии Maven

## Установка Maven на Linux

### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install maven
```

### CentOS/RHEL:
```bash
sudo yum install maven
```

### Проверка:
```bash
mvn -version
```

## Установка Maven на macOS

### С помощью Homebrew:
```bash
brew install maven
```

### Проверка:
```bash
mvn -version
```

## Сборка плагина после установки Maven

1. Откройте командную строку в папке проекта
2. Выполните команду:
```bash
mvn clean package
```

3. Готовый JAR файл будет в папке `target/`

## Альтернативный способ - использование IDE

### IntelliJ IDEA:
1. Откройте проект в IntelliJ IDEA
2. IDE автоматически загрузит Maven wrapper
3. Используйте встроенные инструменты Maven

### Eclipse:
1. Откройте проект в Eclipse
2. Установите плагин Maven для Eclipse
3. Используйте Maven через контекстное меню проекта

### Visual Studio Code:
1. Установите расширение "Maven for Java"
2. Используйте команды Maven через Command Palette

## Устранение неполадок

### Ошибка "mvn не является внутренней или внешней командой"
- Проверьте, что Maven добавлен в PATH
- Перезапустите командную строку
- Убедитесь, что путь к Maven указан правильно

### Ошибка "Java не найдена"
- Установите Java 17 или выше
- Добавьте Java в PATH
- Проверьте версию Java командой `java -version`

### Ошибки при сборке
- Убедитесь, что все файлы проекта на месте
- Проверьте, что версия Java соответствует требованиям
- Очистите проект командой `mvn clean`

## Полезные команды Maven

```bash
mvn clean          # Очистка проекта
mvn compile        # Компиляция
mvn test          # Запуск тестов
mvn package        # Сборка JAR
mvn install        # Установка в локальный репозиторий
mvn clean package  # Очистка + сборка
```

## Ссылки

- [Официальный сайт Maven](https://maven.apache.org/)
- [Скачивание Maven](https://maven.apache.org/download.cgi)
- [Документация Maven](https://maven.apache.org/guides/)
- [Установка Java](https://www.oracle.com/java/technologies/downloads/)
