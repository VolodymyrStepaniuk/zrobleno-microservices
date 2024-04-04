DROP TABLE IF EXISTS public.service_categories;

CREATE TABLE IF NOT EXISTS public.service_categories
(
    id                 bigint                                              NOT NULL DEFAULT nextval('service_categories_id_seq'::regclass),
    title              character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description    text COLLATE pg_catalog."default"                       NOT NULL,
    image_urls         text[] COLLATE pg_catalog."default"                 NOT NULL,
    created_at         timestamp(6) with time zone                         NOT NULL DEFAULT NOW(),
    last_modified_at   timestamp(6) with time zone                         NOT NULL DEFAULT NOW(),
    CONSTRAINT service_categories_pkey PRIMARY KEY (id)
    )
    TABLESPACE pg_default;

ALTER SEQUENCE IF EXISTS public.service_categories_id_seq
    OWNED by public.service_categories.id;

-- Table: public.services

DROP TABLE IF EXISTS public.services;

CREATE TABLE IF NOT EXISTS public.services
(
    id               bigint                                              NOT NULL DEFAULT nextval('services_id_seq'::regclass),
    category_id      bigint                                              NOT NULL,
    owner_id         uuid                                                NOT NULL,
    title            character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description      text COLLATE pg_catalog."default"                   NOT NULL,
    image_urls       text[] COLLATE pg_catalog."default"                 NOT NULL,
    price            numeric                                             NOT NULL,
    priority         integer                                             NOT NULL,
    created_at       timestamp(6) with time zone                         NOT NULL DEFAULT NOW(),
    last_modified_at timestamp(6) with time zone                         NOT NULL DEFAULT NOW(),
    CONSTRAINT services_pkey PRIMARY KEY (id)
    )
    TABLESPACE pg_default;

ALTER SEQUENCE IF EXISTS public.services_id_seq
    OWNED by public.services.id;

-- Table: public.order_statuses

DROP TABLE IF EXISTS public.order_statuses;

CREATE TABLE IF NOT EXISTS public.order_statuses
(
    id       bigint                                              NOT NULL DEFAULT nextval('order_statuses_id_seq'::regclass),
    name     character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT order_statuses_pkey PRIMARY KEY (id),
    CONSTRAINT order_statuses_name_check CHECK (name::text = ANY
                                                (ARRAY ['CREATED'::character varying, 'CANCELED'::character varying, 'CONFIRMED'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying]::text[]))
)
    TABLESPACE pg_default;

ALTER SEQUENCE IF EXISTS public.order_statuses_id_seq
    OWNED by public.order_statuses.id;

-- Table: public.orders

DROP TABLE IF EXISTS public.orders;

CREATE TABLE IF NOT EXISTS public.orders
(
    id               bigint                                              NOT NULL DEFAULT nextval('orders_id_seq'::regclass),
    owner_id         uuid                                                NOT NULL,
    status_id        bigint,
    service_id       bigint                                              NOT NULL,
    comment          character varying(255) COLLATE pg_catalog."default",
    created_at       timestamp(6) with time zone                         NOT NULL DEFAULT NOW(),
    last_modified_at timestamp(6) with time zone                         NOT NULL DEFAULT NOW(),
    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT fkm5letvn141v0flinbgcc1t74q FOREIGN KEY (status_id)
        REFERENCES public.order_statuses (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    TABLESPACE pg_default;

ALTER SEQUENCE IF EXISTS public.orders_id_seq
    OWNED by public.orders.id;

-- Table: public.feedbacks

DROP TABLE IF EXISTS public.feedbacks;

CREATE TABLE IF NOT EXISTS public.feedbacks
(
    id               bigint                      NOT NULL DEFAULT nextval('feedbacks_id_seq'::regclass),
    order_id         bigint                      NOT NULL,
    owner_id         uuid                        NOT NULL,
    rating           integer                     NOT NULL,
    comment          character varying(255) COLLATE pg_catalog."default",
    created_at       timestamp(6) with time zone NOT NULL DEFAULT NOW(),
    last_modified_at timestamp(6) with time zone NOT NULL DEFAULT NOW(),
    CONSTRAINT feedbacks_pkey PRIMARY KEY (id)
)
    TABLESPACE pg_default;

ALTER SEQUENCE IF EXISTS public.feedback_id_seq
    OWNED by public.feedbacks.id;