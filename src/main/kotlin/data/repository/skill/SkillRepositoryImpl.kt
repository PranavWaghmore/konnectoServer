package pw.coding.data.repository.skill

import org.litote.kmongo.coroutine.CoroutineDatabase
import pw.coding.data.models.Skill

class SkillRepositoryImpl(
    db: CoroutineDatabase
): SkillRepository {

    private val skills = db.getCollection<Skill>()

    override suspend fun getSkills(): List<Skill> {
        return skills.find().toList()
    }
}