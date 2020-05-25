package jp.tominaga.atsushi.footprint

import io.realm.RealmObject

open class PhotoInfoModel :RealmObject() {

    //撮影した写真の画像ファイルのContentURI(アプリ間でファイル共有するための保存場所の表示形式)
    var stringContentsUrl: String = ""
    //撮影日時
    var dateTime: String = ""
    //経度
    var latitude: Double = 0.0
    //緯度
    var longitude: Double = 0.0
    //地点(緯度の文字列 + 経度の文字列) => 地図にマーキングする場合を特定させるため
    var location: String =""
    //コメント
    var commen: String = ""
}