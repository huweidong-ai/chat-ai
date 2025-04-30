-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users
(
    id         bigserial                           NOT NULL, -- 用户唯一标识符
    username   varchar(255)                        NOT NULL, -- 用户名，必须唯一
    "password" varchar(255)                        NULL,     -- 用户密码
    email      varchar(255)                        NULL,     -- 用户邮箱，必须唯一
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 记录创建时间
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 记录更新时间
    phone      varchar(20)                         NULL,     -- 手机号
    avatar     varchar(255)                        NULL,     -- 头像
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_phone_key UNIQUE (phone),
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_username_key UNIQUE (username)
);
COMMENT ON TABLE public.users IS '用户表，存储用户的基本信息';

-- Column comments

COMMENT ON COLUMN public.users.id IS '用户唯一标识符';
COMMENT ON COLUMN public.users.username IS '用户名，必须唯一';
COMMENT ON COLUMN public.users."password" IS '用户密码';
COMMENT ON COLUMN public.users.email IS '用户邮箱，必须唯一';
COMMENT ON COLUMN public.users.created_at IS '记录创建时间';
COMMENT ON COLUMN public.users.updated_at IS '记录更新时间';
COMMENT ON COLUMN public.users.phone IS '手机号';
COMMENT ON COLUMN public.users.avatar IS '头像';

CREATE TABLE chat_completions
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    title      VARCHAR(255) NOT NULL,
    model      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE chat_completions IS '聊天完成记录表，存储每次聊天的基本信息';
COMMENT ON COLUMN chat_completions.id IS '聊天完成记录唯一标识符';
COMMENT ON COLUMN chat_completions.user_id IS '关联的用户ID';
COMMENT ON COLUMN chat_completions.title IS '聊天标题';
COMMENT ON COLUMN chat_completions.model IS '使用的模型名称';
COMMENT ON COLUMN chat_completions.created_at IS '记录创建时间';
COMMENT ON COLUMN chat_completions.updated_at IS '记录更新时间';

CREATE TABLE messages
(
    id                 BIGSERIAL PRIMARY KEY,
    chat_completion_id BIGINT      NOT NULL REFERENCES chat_completions (id),
    role               VARCHAR(50) NOT NULL,
    content            TEXT        NOT NULL,
    created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE messages IS '消息表，存储每条消息的内容';
COMMENT ON COLUMN messages.id IS '消息唯一标识符';
COMMENT ON COLUMN messages.chat_completion_id IS '关联的聊天完成记录ID';
COMMENT ON COLUMN messages.role IS '消息发送者的角色';
COMMENT ON COLUMN messages.content IS '消息内容';
COMMENT ON COLUMN messages.created_at IS '记录创建时间';

CREATE TABLE files
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    name       VARCHAR(255) NOT NULL,
    path       VARCHAR(255) NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    size       BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE files IS '文件表，存储用户上传的文件信息';
COMMENT ON COLUMN files.id IS '文件唯一标识符';
COMMENT ON COLUMN files.user_id IS '关联的用户ID';
COMMENT ON COLUMN files.name IS '文件名称';
COMMENT ON COLUMN files.path IS '文件存储路径';
COMMENT ON COLUMN files.type IS '文件类型';
COMMENT ON COLUMN files.size IS '文件大小';
COMMENT ON COLUMN files.created_at IS '记录创建时间';
COMMENT ON COLUMN files.updated_at IS '记录更新时间';