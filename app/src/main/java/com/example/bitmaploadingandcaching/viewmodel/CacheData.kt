package com.example.bitmaploadingandcaching.viewmodel

import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.lifecycle.ViewModel
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.dataclass.LabelData
import com.example.bitmaploadingandcaching.fragments.HomeFragment.Companion.COLORS_LIST
import kotlin.random.Random

class CacheData:ViewModel() {
    companion object{
        var list:MutableList<Contact> = mutableListOf()
        var i = loadContactList()
        private var position = -1
        var bitmapCache = LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory()/4).toInt())
        fun addList(newContact:Contact){
            list.add(0,newContact)
        }

        fun addList(newContact:Contact,pos:Int){
            list.add(pos,newContact)
            position = pos
        }

        private fun loadContactList():MutableList<Contact> {
            list.add(
                Contact(
                    "Dog",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROf6UTZz7xI0CSxn0p8t8IP-tNdjz4HnV7fQ&s",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "98229294892",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Penguin",
                    "https://t4.ftcdn.net/jpg/05/98/80/23/240_F_598802337_8tVqpC3zWLZrRxJwayYdUTM9E3d3xoNG.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "78629649169",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Shark",
                    "https://thumbs.dreamstime.com/b/cute-cartoon-style-shark-ai-generated-301950258.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "987430276659",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Kangaroo",
                    "https://t4.ftcdn.net/jpg/07/10/35/63/240_F_710356348_SHWeoISgEgyv0UaWmfut1rlwpmmAggvu.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "876941097842",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Panda",
                    "https://i.natgeofe.com/k/75ac774d-e6c7-44fa-b787-d0e20742f797/giant-panda-eating_4x3.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7652148835615",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Zebra",
                    "https://c02.purpledshub.com/uploads/sites/62/2014/09/GettyImages-520064858-79cc024.jpg?webp=1&w=1200",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "74872438524",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Dragon",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRqEKYeZnBrX0Qf2ZdUZJEAka4FSQ1uYX6pXw&s",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "76264817674",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Whale",
                    "https://easy-peasy.ai/cdn-cgi/image/quality=80,format=auto,width=700/https://fdczvxmwwjwpwbeeqcth.supabase.co/storage/v1/object/public/images/268c4b03-533f-4e81-aec1-0c22df466a90/eebf3caa-98db-42c0-983f-d6e4b85486e4.png",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "99372925443",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Pegasus",
                    "https://t4.ftcdn.net/jpg/05/62/13/35/360_F_562133561_OIgHUI9hP11y6gWYxdmeNVGtjPHeHvYa.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "962474190174",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Cat",
                    "https//images.pexels.com/photos/104827/cat-pet-animal-domestic-104827.jpeg?cs=srgb&dl=pexels-pixabay-104827.jpg&fm=jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "836194559312",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Griffin",
                    "https//t3.ftcdn.net/jpg/05/84/03/80/360_F_584038065_bTAe8Ly8ZBejUYsJZVJFgYVYGCwbXRtN.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "9842618552484",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Egg",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8iPUqDUNZyP9rYqzg2JXonP7iuacq71NGmw&s",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "76441746882",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Fish Nemo",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLOfj5r_zUKHc9c6zEf00tww8nNwm7Fe1b_Q&s",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "74528551734",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Octopus",
                    "https://as1.ftcdn.net/v2/jpg/05/38/90/32/1000_F_538903298_CDDXJNLeJKEamt5UsZOLViGIMBs2uhG6.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "5483659474",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Boat",
                    "https://i.pinimg.com/originals/9e/d8/c2/9ed8c20db316bb5e9b000485f79d1169.png",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "97258565284",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Ghost",
                    "https://png.pngtree.com/png-vector/20230831/ourmid/pngtree-sticker-halloween-cute-ghost-png-image_9236584.png",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7627578134",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Flower", "https://artincontext.org/wp-content/uploads/2022/05/lily-sketch.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)], "625845242421", "Mobile",false,false,
                    mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Giraffe",
                    "https://t4.ftcdn.net/jpg/07/55/79/77/360_F_755797761_syuApheTCciQYGWhH9BipcULAHb0PLGR.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "85248326592",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Robot", "https://cdn.pixabay.com/photo/2023/05/08/08/41/ai-7977960_1280.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)], "6523857834", "Mobile",false,false,
                    mutableMapOf()
                )
  )
            list.add(Contact(
                    "Clown",
                    "https://static.vecteezy.com/system/resources/previews/026/793/431/original/3d-clown-ai-generated-png.png",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "876295189343",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Robot Dog",
                    "https://m.media-amazon.com/images/I/51MsPhbA9rL._AC_UF1000,1000_QL80_.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "825494650271",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Fox",
                    "https://as2.ftcdn.net/v2/jpg/05/46/87/85/1000_F_546878598_GrpyJuARW7ORzPTRKc2PmWHDwJXuKOaF.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "75421195632",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "White Bat",
                    "https://i.redd.it/ai-generated-art-based-on-album-title-alone-white-bat-v0-ahrc1uudg4oa1.png?width=1024&format=png&auto=webp&s=2b7d61604812010e9830114303aedbb124ee944e",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "64282532254",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Elephant",
                    "https://as2.ftcdn.net/v2/jpg/05/58/18/09/1000_F_558180984_bvzD7FmlT1OXnWJrFepZELt0xHEWCPk4.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "89761292442",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Racoon",
                    "https://t3.ftcdn.net/jpg/01/73/37/16/240_F_173371622_02A2qGqjhsJ5SWVhUPu0t9O9ezlfvF8l.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "976412853423",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Owl",
                    "https://t3.ftcdn.net/jpg/06/57/92/46/240_F_657924687_XCdCxvJWUTAsY9IFoONoCTtDVL7bXmmo.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "86912459242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Deer",
                    "https://t4.ftcdn.net/jpg/00/36/90/85/240_F_36908525_As0ghZR1cwgdeokBZGJirLJmaecyDVFD.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "876215098931",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Parrot",
                    "https://t4.ftcdn.net/jpg/05/69/76/19/240_F_569761902_IJCFEmonuL6ZjRegG95ND6NZMNsYlCUF.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "864219531242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Crab",
                    "https://t4.ftcdn.net/jpg/02/21/29/01/240_F_221290156_nRmQC0NqaFcqqMG6XpAXxWsNXPivO392.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "86125935123",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Turtle",
                    "https://t4.ftcdn.net/jpg/01/97/63/79/240_F_197637933_f6ImHqOfwvUQX0CNKxHjfNd21RDZoCbJ.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7124786873",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Dolphin",
                    "https://t4.ftcdn.net/jpg/03/69/16/01/240_F_369160188_IL1EEplxgVrn5Occa5uSZEWu2w1ON3kW.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "98768352512",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Cola",
                    "https://t3.ftcdn.net/jpg/01/98/52/88/240_F_198528846_0nIrRofL2xrTUJqG9TgIn5hLA6GdFDr6.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "98627151845",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Rhino",
                    "https://t4.ftcdn.net/jpg/03/00/24/41/240_F_300244130_YLQbwdSiZTEzdQFVYJ83EJoDVQZbddA2.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "9875671289521",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Polar Beer",
                    "https://t3.ftcdn.net/jpg/01/26/55/78/240_F_126557872_IrAoU2CN0yIZHntokz2yINUO7GsuE15N.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "967217581352",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Snake",
                    "https://t3.ftcdn.net/jpg/05/03/96/58/240_F_503965831_FD7wnRsLumu7jHoIXqP7Xxd8mie8FEbv.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "89866146242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Eagle",
                    "https://t4.ftcdn.net/jpg/00/42/69/25/240_F_42692507_kYHdiXHXtx0wTSQY4XG1rZDRTy4QjAqK.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "654128751353",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Husky",
                    "https://t4.ftcdn.net/jpg/02/83/11/49/240_F_283114913_4Le6zXwGlc68z9a75cd1lH3a280JIK7D.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "6754127742124",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Duck",
                    "https://t4.ftcdn.net/jpg/04/81/22/35/240_F_481223549_r03hJYLi6Q5Wgd9VcN7j1sQNHmVVDySQ.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "8762487164342",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Sea Lion",
                    "https://t3.ftcdn.net/jpg/06/17/49/84/240_F_617498451_BAzPPZQrg3WmfjUJORVx0cfgPkhsjfTv.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "58236523532",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Cameleon",
                    "https://t3.ftcdn.net/jpg/05/98/73/20/240_F_598732082_xlL59GmevM6BeWOt3tY7Ea98ZDBiYewH.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "6712487634122",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Leopard",
                    "https://t3.ftcdn.net/jpg/05/67/73/32/240_F_567733294_3rTyBiZ72er4JhHVeINMN4SYVylcCxjv.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "871645821674",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Monkey",
                    "https://t4.ftcdn.net/jpg/05/59/29/51/240_F_559295172_M8rFh1rzlRvUPUSvkW9LP5jttL1vLwp7.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7864812867241",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Crocodile",
                    "https://t3.ftcdn.net/jpg/04/09/93/04/240_F_409930418_ZCsIGZVR3Fsx3vlwFXxtvLQcadRY21iR.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "87971249824",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "frog",
                    "https://t3.ftcdn.net/jpg/00/06/74/82/240_F_6748285_nimv7590D4WEeKv86TrgVigSQSP8u3hY.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7621418122412",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "White Tiger",
                    "https://t4.ftcdn.net/jpg/03/05/07/19/240_F_305071929_rpNHnojYD23nsb2MFIdTXIyxZA9MLmu2.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "862489176421",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Sea Horse",
                    "https://t3.ftcdn.net/jpg/06/33/01/94/240_F_633019450_fJc0AePFnzhWyIb0gjszlm0hd2xRZIPq.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "8762174651574",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Star Fish",
                    "https://t3.ftcdn.net/jpg/05/02/61/06/240_F_502610663_0bDKK6BF4S67T7vBzh8fS4575nyZgHvP.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "8767421822",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Tent",
                    "https://t3.ftcdn.net/jpg/06/19/36/18/240_F_619361881_kMhJyU5DXxzy5wVVgcesnfpp0B1zURnL.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7864821242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Tap",
                    "https://t3.ftcdn.net/jpg/03/65/71/50/240_F_365715078_ArWaX2jhn87T0HmHhH6EACia4YO1EGLE.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7861489324",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Lizard",
                    "https://t4.ftcdn.net/jpg/03/27/84/79/240_F_327847948_FGf5gD2I1vzK31MzsRFPUXaYcm7pwzU2.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "987612847924",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "flamingo",
                    "https://t4.ftcdn.net/jpg/06/20/98/53/240_F_620985342_7MLuFUGgSbmB02q1UvFhzIEQ3pUZjyWr.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "42176199824",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Gorilla",
                    "https://t4.ftcdn.net/jpg/05/26/01/81/240_F_526018124_314zcONQdGH5avAhaZuLkPH2pxaaCl0s.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "98712874242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Tiger",
                    "https://t4.ftcdn.net/jpg/05/95/01/35/240_F_595013565_UaM1eVZq3oTBQQguFeO4ElXW5xKeUyIL.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "7657421842",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Little Boy",
                    "https://t4.ftcdn.net/jpg/05/43/03/53/240_F_543035386_WymOyNgssDEL8B16wTM08rltZPB1uw7u.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "76514289412",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Crow",
                    "https://t4.ftcdn.net/jpg/06/78/86/53/240_F_678865337_oJ2abC42oq8jhVoYXbQWl34RXYIfiPff.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "8762419842",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Hyene",
                    "https://t4.ftcdn.net/jpg/03/12/51/11/240_F_312511168_bgj4i8daYsE2mttVxgKOJ09Km96Aq4jI.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "88629461242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Lion",
                    "https://t3.ftcdn.net/jpg/02/93/66/78/240_F_293667824_x1vsWXZMNv0PHb8BILh7pPtNvjPSM4aA.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "82648168242",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Elephant On Tree",
                    "https://t3.ftcdn.net/jpg/02/69/87/04/240_F_269870413_IdnpSWcfCQO7kciAgPZ3y4g38rQLWpHf.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "95185829641",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Butterfly",
                    "https://t3.ftcdn.net/jpg/03/51/40/20/240_F_351402022_Dx6yILBvfp8L7T2QOhsmKgYhHHR4GBuK.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "67196492712",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Bee",
                    "https://t3.ftcdn.net/jpg/05/58/03/78/240_F_558037819_7hLgy7TsdZwCC7qJqmtjRytntOS2TAvc.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "98272340292",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.add(
                Contact(
                    "Bison",
                    "https://t4.ftcdn.net/jpg/04/39/50/87/240_F_439508795_c1HbDXCEVfM1FeP7pVjYAZe2PqMa6DkZ.jpg",
                    COLORS_LIST[Random.nextInt(0, 10)],
                    "75127452729",
                    "Mobile",false,false, mutableMapOf()
                )
            )
            list.sortBy { it.name.uppercase() }
            return list
        }
    }

}