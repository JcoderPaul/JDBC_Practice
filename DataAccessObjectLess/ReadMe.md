Применение принципов data access object (DAO) на практике в простом формате:

- [flight_repository_dao](https://github.com/JcoderPaul/JDBC_Practice/tree/master/DataAccessObjectLess/src/DAOLessonsOne/flight_repository_dao) - папка содержит наши DAO классы, например [TicketDao](https://github.com/JcoderPaul/JDBC_Practice/blob/master/DataAccessObjectLess/src/DAOLessonsOne/flight_repository_dao/TicketDao.java), который реализует принципы CRUD (пока без интерфейса): Create - создание, Read - чтение, Update - обновление, Delete - удаление;
- [connection_util](https://github.com/JcoderPaul/JDBC_Practice/tree/master/DataAccessObjectLess/src/DAOLessonsOne/connection_util) - папка содержит наши классы организующие связь с базой данных;
- [flight_repository_dto](https://github.com/JcoderPaul/JDBC_Practice/tree/master/DataAccessObjectLess/src/DAOLessonsOne/flight_repository_dto) - папка содержит наш Data Transfer Object (DTO) класс - контейнер [Record](https://github.com/JcoderPaul/JDBC_Practice/blob/master/Doc/Records.txt);
- [flight_repository_entity](https://github.com/JcoderPaul/JDBC_Practice/tree/master/DataAccessObjectLess/src/DAOLessonsOne/flight_repository_entity) - папка содержит сущности представляющие таблицы базы данных, например [Ticket](https://github.com/JcoderPaul/JDBC_Practice/blob/master/DataAccessObjectLess/src/DAOLessonsOne/flight_repository_entity/Ticket.java) - аналог нашей таблицы ticket в базе данных ([SQL скрипт созданных таблиц базы](https://github.com/JcoderPaul/JDBC_Practice/blob/master/ConnectLessOne/base/make_base.sql)).

Для тестирования наших DAO классов и взаимодействия их с базой данных применяются: [FindAllWithFilterLess](https://github.com/JcoderPaul/JDBC_Practice/blob/master/DataAccessObjectLess/src/DAOLessonsOne/FindAllWithFilterLess.java), [FindAndUpdateLess](https://github.com/JcoderPaul/JDBC_Practice/blob/master/DataAccessObjectLess/src/DAOLessonsOne/FindAndUpdateLess.java), [SaveAndDeleteLess](https://github.com/JcoderPaul/JDBC_Practice/blob/master/DataAccessObjectLess/src/DAOLessonsOne/SaveAndDeleteLess.java).
Классы и методы стандартных JAVA библиотек и пактов применяемые в этом и других проектах данного раздела находятся в [папке DOC](https://github.com/JcoderPaul/JDBC_Practice/tree/master/Doc).