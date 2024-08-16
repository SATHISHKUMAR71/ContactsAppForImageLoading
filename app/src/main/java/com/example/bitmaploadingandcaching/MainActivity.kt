package com.example.bitmaploadingandcaching


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        var imagesList:MutableList<Contact> = mutableListOf()
        var count = 0
        for (i in 1..1){
            imagesList.add(Contact("Dog","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROf6UTZz7xI0CSxn0p8t8IP-tNdjz4HnV7fQ&s"))
            imagesList.add(Contact("Penguin","https://t4.ftcdn.net/jpg/05/98/80/23/240_F_598802337_8tVqpC3zWLZrRxJwayYdUTM9E3d3xoNG.jpg"))
            imagesList.add(Contact("Shark","https://thumbs.dreamstime.com/b/cute-cartoon-style-shark-ai-generated-301950258.jpg"))
            imagesList.add(Contact("Kangaroo","https://t4.ftcdn.net/jpg/07/10/35/63/240_F_710356348_SHWeoISgEgyv0UaWmfut1rlwpmmAggvu.jpg"))
            imagesList.add(Contact("Panda","https://i.natgeofe.com/k/75ac774d-e6c7-44fa-b787-d0e20742f797/giant-panda-eating_4x3.jpg"))
            imagesList.add(Contact("Zebra","https://c02.purpledshub.com/uploads/sites/62/2014/09/GettyImages-520064858-79cc024.jpg?webp=1&w=1200"))
            imagesList.add(Contact("Dragon","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRqEKYeZnBrX0Qf2ZdUZJEAka4FSQ1uYX6pXw&s"))
            imagesList.add(Contact("Whale","https://easy-peasy.ai/cdn-cgi/image/quality=80,format=auto,width=700/https://fdczvxmwwjwpwbeeqcth.supabase.co/storage/v1/object/public/images/268c4b03-533f-4e81-aec1-0c22df466a90/eebf3caa-98db-42c0-983f-d6e4b85486e4.png"))
            imagesList.add(Contact("Pegasus","https://t4.ftcdn.net/jpg/05/62/13/35/360_F_562133561_OIgHUI9hP11y6gWYxdmeNVGtjPHeHvYa.jpg"))
            imagesList.add(Contact("Cat","https//images.pexels.com/photos/104827/cat-pet-animal-domestic-104827.jpeg?cs=srgb&dl=pexels-pixabay-104827.jpg&fm=jpg"))
            imagesList.add(Contact("Griffin","https//t3.ftcdn.net/jpg/05/84/03/80/360_F_584038065_bTAe8Ly8ZBejUYsJZVJFgYVYGCwbXRtN.jpg"))
            imagesList.add(Contact("Egg","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8iPUqDUNZyP9rYqzg2JXonP7iuacq71NGmw&s"))
            imagesList.add(Contact("Fish Nemo","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLOfj5r_zUKHc9c6zEf00tww8nNwm7Fe1b_Q&s"))
            imagesList.add(Contact("Octopus","https://as1.ftcdn.net/v2/jpg/05/38/90/32/1000_F_538903298_CDDXJNLeJKEamt5UsZOLViGIMBs2uhG6.jpg"))
            imagesList.add(Contact("Boat","https://i.pinimg.com/originals/9e/d8/c2/9ed8c20db316bb5e9b000485f79d1169.png"))
            imagesList.add(Contact("Ghost","https://png.pngtree.com/png-vector/20230831/ourmid/pngtree-sticker-halloween-cute-ghost-png-image_9236584.png"))
            imagesList.add(Contact("Flower","https://artincontext.org/wp-content/uploads/2022/05/lily-sketch.jpg"))
            imagesList.add(Contact("Giraffe","https://t4.ftcdn.net/jpg/07/55/79/77/360_F_755797761_syuApheTCciQYGWhH9BipcULAHb0PLGR.jpg"))
            imagesList.add(Contact("Robot","https://cdn.pixabay.com/photo/2023/05/08/08/41/ai-7977960_1280.jpg"))
            imagesList.add(Contact("Clown","https://static.vecteezy.com/system/resources/previews/026/793/431/original/3d-clown-ai-generated-png.png"))
            imagesList.add(Contact("Robot Dog","https://m.media-amazon.com/images/I/51MsPhbA9rL._AC_UF1000,1000_QL80_.jpg"))
            imagesList.add(Contact("Fox","https://as2.ftcdn.net/v2/jpg/05/46/87/85/1000_F_546878598_GrpyJuARW7ORzPTRKc2PmWHDwJXuKOaF.jpg"))
            imagesList.add(Contact("White Bat","https://i.redd.it/ai-generated-art-based-on-album-title-alone-white-bat-v0-ahrc1uudg4oa1.png?width=1024&format=png&auto=webp&s=2b7d61604812010e9830114303aedbb124ee944e"))
            imagesList.add(Contact("Elephant","https://as2.ftcdn.net/v2/jpg/05/58/18/09/1000_F_558180984_bvzD7FmlT1OXnWJrFepZELt0xHEWCPk4.jpg"))
            imagesList.add(Contact("Racoon","https://t3.ftcdn.net/jpg/01/73/37/16/240_F_173371622_02A2qGqjhsJ5SWVhUPu0t9O9ezlfvF8l.jpg"))
            imagesList.add(Contact("Owl","https://t3.ftcdn.net/jpg/06/57/92/46/240_F_657924687_XCdCxvJWUTAsY9IFoONoCTtDVL7bXmmo.jpg"))
            imagesList.add(Contact("Deer","https://t4.ftcdn.net/jpg/00/36/90/85/240_F_36908525_As0ghZR1cwgdeokBZGJirLJmaecyDVFD.jpg"))
            imagesList.add(Contact("Parrot","https://t4.ftcdn.net/jpg/05/69/76/19/240_F_569761902_IJCFEmonuL6ZjRegG95ND6NZMNsYlCUF.jpg"))
            imagesList.add(Contact("Crab","https://t4.ftcdn.net/jpg/02/21/29/01/240_F_221290156_nRmQC0NqaFcqqMG6XpAXxWsNXPivO392.jpg"))
            imagesList.add(Contact("Turtle","https://t4.ftcdn.net/jpg/01/97/63/79/240_F_197637933_f6ImHqOfwvUQX0CNKxHjfNd21RDZoCbJ.jpg"))
            imagesList.add(Contact("Dolphin","https://t4.ftcdn.net/jpg/03/69/16/01/240_F_369160188_IL1EEplxgVrn5Occa5uSZEWu2w1ON3kW.jpg"))
            imagesList.add(Contact("Cola","https://t3.ftcdn.net/jpg/01/98/52/88/240_F_198528846_0nIrRofL2xrTUJqG9TgIn5hLA6GdFDr6.jpg"))
            imagesList.add(Contact("Rhino","https://t4.ftcdn.net/jpg/03/00/24/41/240_F_300244130_YLQbwdSiZTEzdQFVYJ83EJoDVQZbddA2.jpg"))
            imagesList.add(Contact("Polar Beer","https://t3.ftcdn.net/jpg/01/26/55/78/240_F_126557872_IrAoU2CN0yIZHntokz2yINUO7GsuE15N.jpg"))
            imagesList.add(Contact("Snake","https://t3.ftcdn.net/jpg/05/03/96/58/240_F_503965831_FD7wnRsLumu7jHoIXqP7Xxd8mie8FEbv.jpg"))
            imagesList.add(Contact("Eagle","https://t4.ftcdn.net/jpg/00/42/69/25/240_F_42692507_kYHdiXHXtx0wTSQY4XG1rZDRTy4QjAqK.jpg"))
            imagesList.add(Contact("Husky","https://t4.ftcdn.net/jpg/02/83/11/49/240_F_283114913_4Le6zXwGlc68z9a75cd1lH3a280JIK7D.jpg"))
            imagesList.add(Contact("Duck","https://t4.ftcdn.net/jpg/04/81/22/35/240_F_481223549_r03hJYLi6Q5Wgd9VcN7j1sQNHmVVDySQ.jpg"))
            imagesList.add(Contact("Sea Lion","https://t3.ftcdn.net/jpg/06/17/49/84/240_F_617498451_BAzPPZQrg3WmfjUJORVx0cfgPkhsjfTv.jpg"))
            imagesList.add(Contact("Cameleon","https://t3.ftcdn.net/jpg/05/98/73/20/240_F_598732082_xlL59GmevM6BeWOt3tY7Ea98ZDBiYewH.jpg"))
            imagesList.add(Contact("Leopard","https://t3.ftcdn.net/jpg/05/67/73/32/240_F_567733294_3rTyBiZ72er4JhHVeINMN4SYVylcCxjv.jpg"))
            imagesList.add(Contact("Monkey","https://t4.ftcdn.net/jpg/05/59/29/51/240_F_559295172_M8rFh1rzlRvUPUSvkW9LP5jttL1vLwp7.jpg"))
            imagesList.add(Contact("Crocodile","https://t3.ftcdn.net/jpg/04/09/93/04/240_F_409930418_ZCsIGZVR3Fsx3vlwFXxtvLQcadRY21iR.jpg"))
            imagesList.add(Contact("frog","https://t3.ftcdn.net/jpg/00/06/74/82/240_F_6748285_nimv7590D4WEeKv86TrgVigSQSP8u3hY.jpg"))
            imagesList.add(Contact("White Tiger","https://t4.ftcdn.net/jpg/03/05/07/19/240_F_305071929_rpNHnojYD23nsb2MFIdTXIyxZA9MLmu2.jpg"))
            imagesList.add(Contact("Sea Horse","https://t3.ftcdn.net/jpg/06/33/01/94/240_F_633019450_fJc0AePFnzhWyIb0gjszlm0hd2xRZIPq.jpg"))
            imagesList.add(Contact("Star Fish","https://t3.ftcdn.net/jpg/05/02/61/06/240_F_502610663_0bDKK6BF4S67T7vBzh8fS4575nyZgHvP.jpg"))
            imagesList.add(Contact("Tent","https://t3.ftcdn.net/jpg/06/19/36/18/240_F_619361881_kMhJyU5DXxzy5wVVgcesnfpp0B1zURnL.jpg"))
            imagesList.add(Contact("Tap","https://t3.ftcdn.net/jpg/03/65/71/50/240_F_365715078_ArWaX2jhn87T0HmHhH6EACia4YO1EGLE.jpg"))
            imagesList.add(Contact("Lizard","https://t4.ftcdn.net/jpg/03/27/84/79/240_F_327847948_FGf5gD2I1vzK31MzsRFPUXaYcm7pwzU2.jpg"))
            imagesList.add(Contact("flamingo","https://t4.ftcdn.net/jpg/06/20/98/53/240_F_620985342_7MLuFUGgSbmB02q1UvFhzIEQ3pUZjyWr.jpg"))
            imagesList.add(Contact("Gorilla","https://t4.ftcdn.net/jpg/05/26/01/81/240_F_526018124_314zcONQdGH5avAhaZuLkPH2pxaaCl0s.jpg"))
            imagesList.add(Contact("Tiger","https://t4.ftcdn.net/jpg/05/95/01/35/240_F_595013565_UaM1eVZq3oTBQQguFeO4ElXW5xKeUyIL.jpg"))
            imagesList.add(Contact("Little Boy","https://t4.ftcdn.net/jpg/05/43/03/53/240_F_543035386_WymOyNgssDEL8B16wTM08rltZPB1uw7u.jpg"))
            imagesList.add(Contact("Crow","https://t4.ftcdn.net/jpg/06/78/86/53/240_F_678865337_oJ2abC42oq8jhVoYXbQWl34RXYIfiPff.jpg"))
            imagesList.add(Contact("Hyene","https://t4.ftcdn.net/jpg/03/12/51/11/240_F_312511168_bgj4i8daYsE2mttVxgKOJ09Km96Aq4jI.jpg"))
            imagesList.add(Contact("Lion","https://t3.ftcdn.net/jpg/02/93/66/78/240_F_293667824_x1vsWXZMNv0PHb8BILh7pPtNvjPSM4aA.jpg"))
            imagesList.add(Contact("Elephant On Tree","https://t3.ftcdn.net/jpg/02/69/87/04/240_F_269870413_IdnpSWcfCQO7kciAgPZ3y4g38rQLWpHf.jpg"))
            imagesList.add(Contact("Butterfly","https://t3.ftcdn.net/jpg/03/51/40/20/240_F_351402022_Dx6yILBvfp8L7T2QOhsmKgYhHHR4GBuK.jpg"))
            imagesList.add(Contact("Bee","https://t3.ftcdn.net/jpg/05/58/03/78/240_F_558037819_7hLgy7TsdZwCC7qJqmtjRytntOS2TAvc.jpg"))
            imagesList.add(Contact("Bison","https://t4.ftcdn.net/jpg/04/39/50/87/240_F_439508795_c1HbDXCEVfM1FeP7pVjYAZe2PqMa6DkZ.jpg"))
        }
        imagesList.sortBy {
            it.name
        }
        println("On Download the Item Size: ${imagesList.size/10}")
        rv.adapter = ImagesAdapter(imagesList,baseContext)
        rv.layoutManager = LinearLayoutManager(this)
        println("Max memory: ${ Runtime.getRuntime().maxMemory()}")
    }
}