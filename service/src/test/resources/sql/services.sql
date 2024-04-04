INSERT INTO services (id, category_id, owner_id, title, description, image_urls,
                      price, priority,
                      created_at, last_modified_at)
VALUES(1, 1, 'b869fce1-f221-45bc-9363-f3bec945ac12',
       'First title', 'First description',ARRAY ['https://image.com/1'], 100, 1,
         '2023-11-24T16:22:09.266615Z', '2023-11-25T17:28:19.266615Z'),
      (2, 2, 'b869fce1-f221-45bc-9363-f3bec945ac12',
       'Second title', 'Second description',ARRAY ['https://image.com/2'], 200, 2,
       '2023-11-24T16:22:09.266615Z', '2023-11-25T17:28:19.266615Z'),
      (3, 3, 'b869fce1-f221-45bc-9363-f3bec945ac12',
       'Third title', 'Third description',ARRAY ['https://image.com/3'], 300, 3,
       '2023-11-24T16:22:09.266615Z', '2023-11-25T17:28:19.266615Z');