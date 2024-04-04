INSERT INTO service_categories (id, title, description,
                                image_urls, created_at,
                                last_modified_at)
VALUES (1, 'title', 'description',
        ARRAY ['https://image.com/1'], '2023-11-24T16:22:09.266615Z',
        '2023-11-25T17:28:19.266615Z'),
       (2, 'Second title', 'Second description',
        ARRAY ['https://image.com/2'], '2023-11-24T16:25:09.266615Z',
        '2023-11-25T17:48:19.266615Z'),
       (3, 'Third title', 'Third description',
        ARRAY ['https://image.com/3'], '2023-11-24T16:29:09.266615Z',
        '2023-11-25T17:39:19.266615Z');