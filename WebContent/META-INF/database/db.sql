

/*   Table structure for table `published_item`  */

DROP TABLE IF EXISTS `published_item`;
create table if not exists `published_item` (
  `id` int(11) not null auto_increment,                           /*  id， 主键， 数据库自动生成  */
  `pub_channel` varchar(50) collate utf8_unicode_ci not null,                 /*  youtube、posterous  */
  `pub_type` varchar(50) collate utf8_unicode_ci not null,                    /*  Video, Article, Image, Post  */
  `status` varchar(50) collate utf8_unicode_ci not null,                  /*  状态 fail finished  */
  `remote_id` varchar(50) collate utf8_unicode_ci not null,               /*  space ID/Post ID/video id  */
  `remote_space_id` varchar(50) collate utf8_unicode_ci not null,         /*  Post site id from Posterous  */
  `play_url` varchar(512) collate utf8_unicode_ci default null,               /*  视频的播放地址；博客的访问地址  */
  `user_social_account_id` int(11) not null,                                  /*  Jugnoo user id  */
  `file_token` varchar(50) collate utf8_unicode_ci default null,              /*  等待上传的文件对应的token  */
  `pub_file_name` varchar(250) collate utf8_unicode_ci default null,              /*  标题  */
  `description` varchar(250) collate utf8_unicode_ci default null,        /*  描述  */
  `category` varchar(250) collate utf8_unicode_ci default null,           /*  分类  */
  `keywords` varchar(250) collate utf8_unicode_ci default null,           /*  关键字  */
  `tags` varchar(50) collate utf8_unicode_ci default null,                /*   jugnoo embedded tags  */
  `cb_url` varchar(512) collate utf8_unicode_ci default null,             /*  回调URL  */
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  primary key (`id`)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci 
;

DROP TABLE IF EXISTS `stats`;
create table if not exists `stats` (
  `published_item_id` int(11),
  `view_count` int(16) not null,                                   /*  观看次数  */
  `comment_count` int(16) not null,                                /*  评论次数  */
  `like_count` int(16) not null,                                   /*  收藏次数  */
  `dislike_count` int(16) not null,                                /*  收藏次数  */
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  primary key (`published_item_id`)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci 
;

/*   Table structure for table `videos_stasts_history`  */
/*   历史数据，记录视频的观看次数、评论次数、收藏次数  */

DROP TABLE IF EXISTS `stats_history`;
create table if not exists `stats_history` (
  `id` int(11) not null auto_increment, 
  `published_item_id` int(11),
  `view_count` int(16) not null,                                   /*  观看次数  */
  `comment_count` int(16) not null,                                /*  评论次数  */
  `like_count` int(16) not null,                                   /*  收藏次数  */
  `dislike_count` int(16) not null,                                /*  收藏次数  */
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  primary key (`id`),
  unique index stats_history_2_7 (`published_item_id`, `created_at`)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci 
;

--
-- Table structure for table `user_social_accounts`
--

CREATE TABLE IF NOT EXISTS `user_social_accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_token` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_profile_name` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `social_account_id` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `sa_id` int(11) DEFAULT NULL,
  `user_password` varchar(255) DEFAULT NULL,
  `secret` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci 
;


